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
import cn.momia.service.deal.web.ctrl.dto.OrderDupDto;
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
import org.apache.commons.lang3.StringUtils;
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

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage add(@RequestBody Order order) {
        Sku sku = ProductServiceApi.SKU.get(order.getProductId(), order.getSkuId());
        checkOrder(order, sku);

        if (!lockSku(order)) return ResponseMessage.FAILED("库存不足");

        long orderId = 0;
        try {
            processContacts(order.getCustomerId(), order.getMobile(), order.getContacts());
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
                sku.isClosed())  throw new MomiaFailedException("活动已报满或结束，不能再下单");

        if (sku.isNeedRealName() &&
                (order.getParticipants() == null ||
                        order.getParticipants().isEmpty() ||
                        order.getParticipants().size() != order.getJoinedCount())) throw new MomiaFailedException("无效的订单，出行人信息不完整");
        if (order.getParticipants() != null && !order.getParticipants().isEmpty()) UserServiceApi.PARTICIPANT.checkParticipants(order.getCustomerId(), order.getParticipants());

        for (OrderPrice price : order.getPrices()) {
            if (!sku.findPrice(price.getAdult(), price.getChild(), price.getPrice())) throw new MomiaFailedException("无效的订单，套餐不正确");
        }
    }

    private boolean lockSku(Order order) {
        return ProductServiceApi.SKU.lockStock(order.getProductId(), order.getSkuId(), order.getCount(), order.getJoinedCount());
    }

    private void processContacts(long userId, String mobile, String name) {
        try {
            if (MobileUtil.isInvalidMobile(mobile) || StringUtils.isBlank(name)) return;
            UserServiceApi.USER.processContacts(userId, mobile, name);
        } catch (Exception e) {
            LOGGER.error("error occurred during process contacts {}/{}", mobile, name, e);
        }
    }

    private boolean unlockSku(Order order) {
        return ProductServiceApi.SKU.unlockStock(order.getProductId(), order.getSkuId(), order.getCount(), order.getJoinedCount());
    }

    @RequestMapping(value = "/check/dup", method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage checkDup(@RequestBody Order order) {
        try {
            List<Order> orders = dealServiceFacade.getOrders(order.getCustomerId(), order.getProductId(), order.getSkuId());
            if (orders.isEmpty()) return ResponseMessage.SUCCESS(OrderDupDto.NOT_DUPLICATED);

            for (Order o : orders) {
                if (o.isPayed()) continue;

                OrderDupDto orderDupDto = new OrderDupDto();
                orderDupDto.setDuplicated(true);
                orderDupDto.setOrderId(o.getId());
                if (isSame(order, o)) orderDupDto.setSame(true);

                return ResponseMessage.SUCCESS(orderDupDto);
            }
        } catch (Exception e) {
            return ResponseMessage.SUCCESS(OrderDupDto.NOT_DUPLICATED);
        }

        return ResponseMessage.SUCCESS(OrderDupDto.NOT_DUPLICATED);
    }

    private boolean isSame(Order order1, Order order2) {
        if (!isSamePrices(order1.getPrices(), order2.getPrices())) return false;
        if (!isSameContacts(order1.getContacts(), order2.getContacts())) return false;
        if (!isSameMobile(order1.getMobile(), order2.getMobile())) return false;
        if (!isSameParticipants(order1.getParticipants(), order2.getParticipants())) return false;
        return true;
    }

    private boolean isSamePrices(List<OrderPrice> prices1, List<OrderPrice> prices2) {
        if (prices1 == null && prices2 == null) return true;
        if (prices1 != null && prices2 != null) {
            for (OrderPrice price : prices1) {
                if (!prices2.contains(price)) return false;
            }

            for (OrderPrice price : prices2) {
                if (!prices1.contains(price)) return false;
            }

            return true;
        }
        return false;
    }

    private boolean isSameContacts(String contacts1, String contacts2) {
        if (contacts1 == null && contacts2 == null) return true;
        if (contacts1 != null && contacts2 != null) return contacts1.equals(contacts2);
        return false;
    }

    private boolean isSameMobile(String mobile1, String mobile2) {
        if (mobile1 == null && mobile2 == null) return true;
        if (mobile1 != null && mobile2 != null) return mobile1.equals(mobile2);
        return false;
    }

    private boolean isSameParticipants(List<Long> participants1, List<Long> participants2) {
        if (participants1 == null && participants2 == null) return true;
        if (participants1 != null && participants2 != null) {
            for (long id : participants1) {
                if (!participants2.contains(id)) return false;
            }

            for (long id : participants2) {
                if (!participants1.contains(id)) return false;
            }

            return true;
        }
        return false;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage delete(@RequestParam String utoken, @PathVariable long id) {
        User user = UserServiceApi.USER.get(utoken);
        Order order = dealServiceFacade.getOrder(id);
        if (!order.exists() || order.getCustomerId() != user.getId()) return ResponseMessage.FAILED("无效的订单");

        if (!dealServiceFacade.deleteOrder(user.getId(), id)) return ResponseMessage.FAILED("删除订单失败");

        // TODO 需要告警
        if (!unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });

        int status = order.getStatus();
        if (status == Order.Status.PRE_PAYED) promoServiceFacade.releaseUserCoupon(order.getCustomerId(), order.getId());

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseMessage list(@RequestParam String utoken,
                                @RequestParam int status,
                                @RequestParam int start,
                                @RequestParam int count) {
        if (isInvalidLimit(start, count)) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        User user = UserServiceApi.USER.get(utoken);
        long totalCount = dealServiceFacade.queryOrderCountByUser(user.getId(), status);
        List<Order> orders = dealServiceFacade.queryOrderByUser(user.getId(), status, start, count);

        List<Long> productIds = new ArrayList<Long>();
        for (Order order : orders) productIds.add(order.getProductId());
        List<Product> products = ProductServiceApi.PRODUCT.list(productIds);

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

    @RequestMapping(value = "/{id}/check", method = RequestMethod.GET)
    public ResponseMessage check(@RequestParam String utoken,
                                  @PathVariable(value = "id") long id,
                                  @RequestParam(value = "pid") long productId,
                                  @RequestParam(value = "sid") long skuId) {
        User user = UserServiceApi.USER.get(utoken);
        Order order = dealServiceFacade.getOrder(id);
        if (!order.isPayed() ||
                order.getCustomerId() != user.getId() ||
                order.getProductId() != productId ||
                order.getSkuId() != skuId) return ResponseMessage.SUCCESS(false);

        return ResponseMessage.SUCCESS(true);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage detail(@RequestParam String utoken,
                                  @PathVariable(value = "id") long id,
                                  @RequestParam(value = "pid") long productId) {
        User user = UserServiceApi.USER.get(utoken);
        Order order = dealServiceFacade.getOrder(id);
        Product product = ProductServiceApi.PRODUCT.get(productId, Product.Type.BASE_WITH_SKU);
        if (!order.exists() || !product.exists() ||
                order.getCustomerId() != user.getId() ||
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
        List<User> customers = UserServiceApi.USER.list(customerIds, User.Type.MINI);
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
        Map<Long, List<Long>> skuCustomerIdsMap = new HashMap<Long, List<Long>>();
        Set<Long> customerIds = new HashSet<Long>();

        for (Order order: orders) {
            long skuId = order.getSkuId();
            long customerId = order.getCustomerId();

            List<Order> skuOrders = skuOrdersMap.get(skuId);
            if (skuOrders == null) {
                skuOrders = new ArrayList<Order>();
                skuOrdersMap.put(skuId, skuOrders);
            }
            skuOrders.add(order);

            List<Long> skuCustomerIds = skuCustomerIdsMap.get(skuId);
            if (skuCustomerIds == null) {
                skuCustomerIds = new ArrayList<Long>();
                skuCustomerIdsMap.put(skuId, skuCustomerIds);
            }
            if (!skuCustomerIds.contains(customerId)) skuCustomerIds.add(customerId);
            customerIds.add(customerId);
        }

        Map<Long, User> customersMap = new HashMap<Long, User>();
        for (User customer : UserServiceApi.USER.list(customerIds, User.Type.FULL)) {
            customersMap.put(customer.getId(), customer);
        }

        return ResponseMessage.SUCCESS(buildPlaymates(skus, skuOrdersMap, skuCustomerIdsMap, customersMap));
    }

    private List<Sku> querySkus(long id, int start, int count) {
        List<Sku> skus = ProductServiceApi.SKU.listAll(id);

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
                                                 Map<Long, List<Long>> skuCustomerIdsMap,
                                                 Map<Long, User> customersMap) {
        List<SkuPlaymatesDto> skusPlaymatesDto = new ArrayList<SkuPlaymatesDto>();
        for (Sku sku : skus) {
            try {
                SkuPlaymatesDto skuPlaymatesDto = new SkuPlaymatesDto();
                skuPlaymatesDto.setTime(sku.getTime());
                skuPlaymatesDto.setJoined(formatJoined(skuOrdersMap.get(sku.getSkuId())));
                skuPlaymatesDto.setPlaymates(extractPlayMates(sku.getSkuId(), skuCustomerIdsMap, customersMap));

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

    private List<PlaymateDto> extractPlayMates(long skuId, Map<Long, List<Long>> skuCustomerIdsMap, Map<Long, User> customersMap) {
        int pageSize = Configuration.getInt("Product.Playmate.PageSize");
        List<PlaymateDto> playmatesDto = new ArrayList<PlaymateDto>();
        List<Long> customerIds = skuCustomerIdsMap.get(skuId);
        if (customerIds != null) {
            for (long customerId : customerIds) {
                PlaymateDto playmateDto = new PlaymateDto();
                User customer = customersMap.get(customerId);
                if (customer == null) continue;
                playmateDto.setId(customer.getId());
                playmateDto.setNickName(customer.getNickName());
                playmateDto.setAvatar(customer.getAvatar());

                List<String> childrenStrs = new ArrayList<String>();
                List<Participant> children = customer.getChildren();
                if (children != null) {
                    for (Participant child : children) {
                        childrenStrs.add(child.getSex() + "孩" + TimeUtil.getAgeDesc(child.getBirthday()));
                    }
                }
                playmateDto.setChildren(childrenStrs);

                if (playmatesDto.size() < pageSize) playmatesDto.add(playmateDto);
                else break;
            }
        }

        return playmatesDto;
    }
}
