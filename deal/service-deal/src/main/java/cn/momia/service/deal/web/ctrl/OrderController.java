package cn.momia.service.deal.web.ctrl;

import cn.momia.service.base.config.Configuration;
import cn.momia.service.base.util.MobileUtil;
import cn.momia.api.base.exception.MomiaFailedException;
import cn.momia.service.base.util.TimeUtil;
import cn.momia.service.base.web.ctrl.AbstractController;
import cn.momia.service.deal.exception.OrderLimitException;
import cn.momia.service.deal.facade.DealServiceFacade;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderPrice;
import cn.momia.service.deal.web.ctrl.dto.OrderDetailDto;
import cn.momia.service.deal.web.ctrl.dto.OrderDto;
import cn.momia.service.deal.web.ctrl.dto.PlaymateDto;
import cn.momia.service.deal.web.ctrl.dto.SkuPlaymatesDto;
import cn.momia.api.product.ProductServiceApi;
import cn.momia.api.product.Product;
import cn.momia.api.product.sku.Sku;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.participant.Participant;
import cn.momia.api.user.User;
import cn.momia.service.base.web.ctrl.dto.ListDto;
import cn.momia.service.base.web.ctrl.dto.PagedListDto;
import cn.momia.service.base.web.response.ResponseMessage;
import cn.momia.service.promo.facade.PromoServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/order")
public class OrderController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired private DealServiceFacade dealServiceFacade;
    @Autowired private PromoServiceFacade promoServiceFacade;

    @Autowired private ProductServiceApi productServiceApi;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage add(@RequestBody Order order) {
        Sku sku = productServiceApi.SKU.get(order.getProductId(), order.getSkuId());
        checkOrder(order, sku);

        if (!lockSku(order)) return ResponseMessage.FAILED("库存不足");

        long orderId = 0;
        try {
            dealServiceFacade.checkLimit(order.getCustomerId(), sku.getSkuId(), order.getCount(), sku.getLimit());

            orderId = dealServiceFacade.placeOrder(order);
            if (orderId > 0) {
                order.setId(orderId);
                return ResponseMessage.SUCCESS(new OrderDto(order));
            }
        } catch (OrderLimitException e) {
            return ResponseMessage.FAILED("本单有限购，您已超出购买限额");
        } catch (Exception e) {
            LOGGER.error("fail to place order, customerId: {}, productId: {}, skuId: {}", new Object[] { order.getCustomerId(), order.getProductId(), order.getSkuId(), e });
        } finally {
            // TODO 需要告警
            if (orderId <= 0 && !unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });
        }

        return ResponseMessage.FAILED("下单失败");
    }

    private void checkOrder(Order order, Sku sku) {
        if (MobileUtil.isInvalidMobile(order.getMobile())) throw new MomiaFailedException("无效的联系电话");
        if (order.getCustomerId() <= 0 ||
                order.getProductId() <= 0 ||
                order.getSkuId() <= 0 ||
                order.getPrices().isEmpty() ||
                !sku.exists() ||
                sku.getProductId() != order.getProductId() ||
                sku.isClosed())  throw new MomiaFailedException("活动已结束或下线，不能再下单");

        if (sku.isNeedRealName() && (order.getParticipants() == null || order.getParticipants().isEmpty())) throw new MomiaFailedException("无效的订单，缺少出行人");
        if (order.getParticipants() != null && !order.getParticipants().isEmpty()) userServiceApi.PARTICIPANT.checkParticipants(order.getCustomerId(), order.getParticipants());

        for (OrderPrice price : order.getPrices()) {
            if (!sku.findPrice(price.getAdult(), price.getChild(), price.getPrice())) throw new MomiaFailedException("无效的订单，套餐不正确");
        }
    }

    private boolean lockSku(Order order) {
        return productServiceApi.SKU.lockStock(order.getProductId(), order.getSkuId(), order.getCount(), order.getJoinedCount());
    }

    private boolean unlockSku(Order order) {
        return productServiceApi.SKU.unlockStock(order.getProductId(), order.getSkuId(), order.getCount(), order.getJoinedCount());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage delete(@RequestParam(value = "uid") long userId, @PathVariable long id) {
        Order order = dealServiceFacade.getOrder(id);
        if (!order.exists() || order.getCustomerId() != userId) return ResponseMessage.FAILED("无效的订单");

        if (!dealServiceFacade.deleteOrder(userId, id)) return ResponseMessage.FAILED("删除订单失败");

        // TODO 需要告警
        if (!unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });

        int status = order.getStatus();
        if (status == Order.Status.PRE_PAYED) promoServiceFacade.releaseUserCoupon(order.getCustomerId(), order.getId());

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseMessage list(@RequestParam(value = "uid") long userId,
                                @RequestParam int status,
                                @RequestParam int start,
                                @RequestParam int count) {
        if (isInvalidLimit(start, count)) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        long totalCount = dealServiceFacade.queryOrderCountByUser(userId, status);
        List<Order> orders = dealServiceFacade.queryOrderByUser(userId, status, start, count);

        List<Long> productIds = new ArrayList<Long>();
        for (Order order : orders) productIds.add(order.getProductId());
        List<Product> products = productServiceApi.PRODUCT.list(productIds);

        return ResponseMessage.SUCCESS(buildUserOrders(totalCount, orders, products, start, count));
    }

    private PagedListDto buildUserOrders(long totalCount, List<Order> orders, List<Product> products, int start, int count) {
        Map<Long, Product> productMap = new HashMap<Long, Product>();
        for (Product product : products) productMap.put(product.getId(), product);

        PagedListDto userOrdersDto = new PagedListDto(totalCount, start, count);
        for (Order order : orders) {
            try {
                Product product = productMap.get(order.getProductId());
                if (product == null) continue;

                userOrdersDto.add(new OrderDetailDto(order, product));
            } catch (Exception e) {
                LOGGER.error("fail to build order dto for order: {}", order.getId(), e);
            }
        }

        return userOrdersDto;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage detail(@RequestParam(value = "uid") long userId,
                                  @PathVariable(value = "id") long id,
                                  @RequestParam(value = "pid") long productId) {
        Order order = dealServiceFacade.getOrder(id);
        Product product = productServiceApi.PRODUCT.get(productId, Product.Type.BASE_WITH_SKU);
        if (!order.exists() || !product.exists() ||
                order.getCustomerId() != userId ||
                order.getProductId() != product.getId()) return ResponseMessage.FAILED("无效的订单");

        return ResponseMessage.SUCCESS(new OrderDetailDto(order, product));
    }

    @RequestMapping(value = "/customer", method = RequestMethod.GET)
    public ResponseMessage getProductCustomersInfo(@RequestParam(value = "pid") long productId, @RequestParam int count) {
        if (productId <= 0 || count <= 0) return ResponseMessage.BAD_REQUEST;

        List<Order> orders = dealServiceFacade.queryDistinctCustomerOrderByProduct(productId, 0, count);
        if (orders.isEmpty()) return ResponseMessage.SUCCESS(ListDto.EMPTY);

        List<Long> customerIds = new ArrayList<Long>();
        for (Order order : orders) customerIds.add(order.getCustomerId());
        List<User> customers = userServiceApi.USER.list(customerIds, User.Type.MINI);
        Map<Long, User> customersMap = new HashMap<Long, User>();
        for (User customer : customers) {
            customersMap.put(customer.getId(), customer);
        }

        ListDto avatars = new ListDto();
        for (Order order : orders) {
            long customerId = order.getCustomerId();
            User customer = customersMap.get(customerId);
            if (customer == null) continue;

            avatars.add(customer.getAvatar());
        }

        return ResponseMessage.SUCCESS(avatars);
    }

    @RequestMapping(value = "/playmate", method = RequestMethod.GET)
    public ResponseMessage listPlaymates(@RequestParam(value = "pid") long productId, @RequestParam int start, @RequestParam int count) {
        if (productId <= 0 || isInvalidLimit(start, count)) return ResponseMessage.BAD_REQUEST;

        List<Sku> skus = querySkus(productId, start, count);
        if (skus.isEmpty()) return ResponseMessage.EMPTY_ARRAY;

        List<Order> orders = queryOrders(productId, skus);
        if (orders.isEmpty()) return ResponseMessage.EMPTY_ARRAY;

        Map<Long, List<Order>> skuOrdersMap = new HashMap<Long, List<Order>>();
        Map<Long, Set<Long>> skuCustomerIdsMap = new HashMap<Long, Set<Long>>();
        Set<Long> customerIds = new HashSet<Long>();
        Set<Long> participantIds = new HashSet<Long>();
        Map<Long, Set<Long>> customerPrticipantsIdsMap = new HashMap<Long, Set<Long>>();

        for (Order order: orders) {
            long skuId = order.getSkuId();
            long customerId = order.getCustomerId();
            List<Long> orderParticipantIds = order.getParticipants();

            List<Order> skuOrders = skuOrdersMap.get(skuId);
            if (skuOrders == null) {
                skuOrders = new ArrayList<Order>();
                skuOrdersMap.put(skuId, skuOrders);
            }
            skuOrders.add(order);

            Set<Long> skuCustomerIds = skuCustomerIdsMap.get(skuId);
            if (skuCustomerIds == null) {
                skuCustomerIds = new HashSet<Long>();
                skuCustomerIdsMap.put(skuId, skuCustomerIds);
            }
            skuCustomerIds.add(customerId);

            customerIds.add(customerId);
            participantIds.addAll(orderParticipantIds);

            Set<Long> customerParticipantsIds = customerPrticipantsIdsMap.get(customerId);
            if (customerParticipantsIds == null) {
                customerParticipantsIds = new HashSet<Long>();
                customerPrticipantsIdsMap.put(customerId, customerParticipantsIds);
            }
            customerParticipantsIds.addAll(orderParticipantIds);
        }

        Map<Long, User> customersMap = new HashMap<Long, User>();
        for (User customer : userServiceApi.USER.list(customerIds, User.Type.MINI)) {
            customersMap.put(customer.getId(), customer);
        }

        Map<Long, Participant> participantsMap = new HashMap<Long, Participant>();
        for (Participant participant : userServiceApi.PARTICIPANT.list(participantIds)) {
            participantsMap.put(participant.getId(), participant);
        }

        return ResponseMessage.SUCCESS(buildPlaymates(skus, skuOrdersMap, skuCustomerIdsMap, customerPrticipantsIdsMap, customersMap, participantsMap));
    }

    private List<Sku> querySkus(long id, int start, int count) {
        List<Sku> skus = productServiceApi.SKU.listAll(id);

        List<Sku> result = new ArrayList<Sku>();
        for (int i = start; i < Math.min(skus.size(), start + count); i++) {
            result.add(skus.get(i));
        }

        return result;
    }

    private List<Order> queryOrders(long id, List<Sku> skus) {
        Set<Long> skuIds = new HashSet<Long>();
        for (Sku sku : skus) {
            skuIds.add(sku.getSkuId());
        }

        // TODO 性能优化
        List<Order> result = new ArrayList<Order>();
        List<Order> orders = dealServiceFacade.queryAllCustomerOrderByProduct(id);
        for (Order order : orders) {
            if (skuIds.contains(order.getSkuId())) result.add(order);
        }

        return result;
    }

    private List<SkuPlaymatesDto> buildPlaymates(List<Sku> skus,
                                                 Map<Long, List<Order>> skuOrdersMap,
                                                 Map<Long, Set<Long>> skuCustomerIdsMap,
                                                 Map<Long, Set<Long>> customerPrticipantsIdsMap,
                                                 Map<Long, User> customersMap,
                                                 Map<Long, Participant> participantsMap) {
        List<SkuPlaymatesDto> skusPlaymatesDto = new ArrayList<SkuPlaymatesDto>();
        for (Sku sku : skus) {
            try {
                SkuPlaymatesDto skuPlaymatesDto = new SkuPlaymatesDto();
                skuPlaymatesDto.setTime(sku.getTime());
                skuPlaymatesDto.setJoined(formatJoined(skuOrdersMap.get(sku.getSkuId())));
                skuPlaymatesDto.setPlaymates(extractPlayMates(sku.getSkuId(), skuCustomerIdsMap, customerPrticipantsIdsMap, customersMap, participantsMap));

                skusPlaymatesDto.add(skuPlaymatesDto);
            } catch (Exception e) {
                LOGGER.error("fail to build playmate for sku: {}", sku.getSkuId(), e);
            }
        }

        return skusPlaymatesDto;
    }

    private String formatJoined(List<Order> orders) {
        int adultCount = 0;
        int childCount = 0;
        if (orders != null) {
            for (Order order : orders) {
                adultCount += order.getAdultCount();
                childCount += order.getChildCount();
            }
        }

        int totalCount = adultCount + childCount;

        StringBuilder builder = new StringBuilder();
        builder.append(totalCount).append("人已报名");
        if (adultCount > 0 || childCount > 0) {
            builder.append("(");
            if (adultCount > 0 && childCount <= 0) {
                builder.append(adultCount).append("成人");
            } else if (adultCount <= 0 && childCount > 0) {
                builder.append(childCount).append("儿童");
            } else {
                builder.append(adultCount).append("成人")
                        .append("，")
                        .append(childCount).append("儿童");
            }
            builder.append(")");
        }

        return builder.toString();
    }

    private List<PlaymateDto> extractPlayMates(long skuId,
                                               Map<Long, Set<Long>> skuCustomerIdsMap,
                                               Map<Long, Set<Long>> customerPrticipantsIdsMap,
                                               Map<Long, User> customersMap,
                                               Map<Long, Participant> participantsMap) {
        int pageSize = Configuration.getInt("Product.Playmate.PageSize");
        List<PlaymateDto> playmatesDto = new ArrayList<PlaymateDto>();
        Set<Long> customerIds = skuCustomerIdsMap.get(skuId);
        if (customerIds != null) {
            for (long customerId : customerIds) {
                PlaymateDto playmateDto = new PlaymateDto();
                User customer = customersMap.get(customerId);
                if (customer == null) continue;
                playmateDto.setId(customer.getId());
                playmateDto.setNickName(customer.getNickName());
                playmateDto.setAvatar(customer.getAvatar());

                List<String> children = new ArrayList<String>();
                Set<Long> customerPrticipantsIds = customerPrticipantsIdsMap.get(customerId);
                if (customerPrticipantsIds != null) {
                    for (long participantId : customerPrticipantsIds) {
                        Participant participant = participantsMap.get(participantId);
                        if (participant != null && TimeUtil.isChild(participant.getBirthday())) {
                            children.add(participant.getSex() + "孩" + TimeUtil.getAge(participant.getBirthday()) + "岁");
                        }
                    }
                }
                playmateDto.setChildren(children);

                if (playmatesDto.size() < pageSize) playmatesDto.add(playmateDto);
            }
        }

        return playmatesDto;
    }
}
