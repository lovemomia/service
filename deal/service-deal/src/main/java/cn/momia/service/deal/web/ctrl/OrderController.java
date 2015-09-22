package cn.momia.service.deal.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.deal.dto.OrderDto;
import cn.momia.api.deal.dto.OrderDupDto;
import cn.momia.api.deal.dto.PlaymateDto;
import cn.momia.api.deal.dto.SkuPlaymatesDto;
import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.MobileUtil;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.api.dto.ListDto;
import cn.momia.common.api.dto.PagedListDto;
import cn.momia.service.order.product.OrderLimitException;
import cn.momia.service.order.product.Order;
import cn.momia.service.order.product.OrderPrice;
import cn.momia.service.order.product.OrderService;
import cn.momia.api.product.ProductServiceApi;
import cn.momia.api.product.entity.Product;
import cn.momia.api.product.entity.Sku;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.ParticipantDto;
import cn.momia.api.user.dto.UserDto;
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
public class OrderController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired private OrderService orderService;
    @Autowired private PromoServiceFacade promoServiceFacade;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse add(@RequestBody Order order) {
        Sku sku = ProductServiceApi.SKU.get(order.getProductId(), order.getSkuId());
        checkOrder(order, sku);

        if (!lockSku(order)) return MomiaHttpResponse.FAILED("库存不足");

        long orderId = 0;
        try {
            processContacts(order.getCustomerId(), order.getMobile(), order.getContacts());
            orderService.checkLimit(order.getCustomerId(), sku.getSkuId(), order.getCount(), sku.getLimit());

            orderId = orderService.add(order);
            if (orderId > 0) {
                order.setId(orderId);
                return MomiaHttpResponse.SUCCESS(buildOrderDto(order));
            }
        } catch (OrderLimitException e) {
            return MomiaHttpResponse.FAILED("本单有限购，您已超出购买限额");
        } catch (Exception e) {
            LOGGER.error("fail to place order, customerId: {}, productId: {}, skuId: {}", new Object[] { order.getCustomerId(), order.getProductId(), order.getSkuId(), e });
        } finally {
            // TODO 需要告警
            if (orderId <= 0 && !unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });
        }

        return MomiaHttpResponse.FAILED("下单失败");
    }

    private void checkOrder(Order order, Sku sku) {
        if (MobileUtil.isInvalid(order.getMobile())) throw new MomiaFailedException("无效的联系电话");
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
            if (MobileUtil.isInvalid(mobile) || StringUtils.isBlank(name)) return;
            UserServiceApi.USER.setContacts(userId, mobile, name);
        } catch (Exception e) {
            LOGGER.error("error occurred during process contacts {}/{}", mobile, name, e);
        }
    }

    private OrderDto buildOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setProductId(order.getProductId());
        orderDto.setSkuId(order.getSkuId());
        orderDto.setCount(order.getCount());
        orderDto.setTotalFee(order.getTotalFee());
        orderDto.setParticipants(buildParticipants(order.getPrices()));
        orderDto.setContacts(order.getContacts());
        orderDto.setMobile(MobileUtil.encrypt(order.getMobile()));
        orderDto.setAddTime(order.getAddTime());
        orderDto.setStatus(order.getStatus());
        orderDto.setPayed(order.isPayed());

        return orderDto;
    }

    private String buildParticipants(List<OrderPrice> prices) {
        int adult = 0;
        int child = 0;
        for (OrderPrice price : prices) {
            adult += price.getAdult() * price.getCount();
            child += price.getChild() * price.getCount();
        }

        if (adult > 0 && child > 0) return adult + "成人, " + child + "儿童";
        else if (adult <= 0 && child > 0) return child + "儿童";
        else if (adult > 0 && child <= 0) return adult + "成人";
        return "";
    }

    private boolean unlockSku(Order order) {
        return ProductServiceApi.SKU.unlockStock(order.getProductId(), order.getSkuId(), order.getCount(), order.getJoinedCount());
    }

    @RequestMapping(value = "/check/dup", method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse checkDup(@RequestBody Order order) {
        try {
            List<Order> orders = orderService.list(order.getCustomerId(), order.getProductId(), order.getSkuId());
            if (orders.isEmpty()) return MomiaHttpResponse.SUCCESS(OrderDupDto.NOT_DUPLICATED);

            for (Order o : orders) {
                if (o.isPayed()) continue;

                OrderDupDto orderDupDto = new OrderDupDto();
                orderDupDto.setDuplicated(true);
                orderDupDto.setOrderId(o.getId());
                orderDupDto.setProductId(o.getProductId());
                if (isSame(order, o)) orderDupDto.setSame(true);

                return MomiaHttpResponse.SUCCESS(orderDupDto);
            }
        } catch (Exception e) {
            return MomiaHttpResponse.SUCCESS(OrderDupDto.NOT_DUPLICATED);
        }

        return MomiaHttpResponse.SUCCESS(OrderDupDto.NOT_DUPLICATED);
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
    public MomiaHttpResponse delete(@RequestParam String utoken, @PathVariable long id) {
        UserDto user = UserServiceApi.USER.get(utoken);
        Order order = orderService.get(id);
        if (!order.exists() || order.getCustomerId() != user.getId()) return MomiaHttpResponse.FAILED("无效的订单");

        if (!orderService.delete(user.getId(), id)) return MomiaHttpResponse.FAILED("删除订单失败");

        // TODO 需要告警
        if (!unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });

        int status = order.getStatus();
        if (status == Order.Status.PRE_PAYED) promoServiceFacade.releaseUserCoupon(order.getCustomerId(), order.getId());

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam String utoken,
                                  @RequestParam int status,
                                  @RequestParam int start,
                                  @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedListDto.EMPTY);

        UserDto user = UserServiceApi.USER.get(utoken);
        long totalCount = orderService.queryCountByUser(user.getId(), status);
        List<Order> orders = orderService.queryByUser(user.getId(), status, start, count);

        List<Long> productIds = new ArrayList<Long>();
        for (Order order : orders) productIds.add(order.getProductId());
        List<Product> products = ProductServiceApi.PRODUCT.list(productIds);

        return MomiaHttpResponse.SUCCESS(buildUserOrders(totalCount, orders, products, start, count));
    }

    private PagedListDto buildUserOrders(long totalCount, List<Order> orders, List<Product> products, int start, int count) {
        Map<Long, Product> productMap = new HashMap<Long, Product>();
        for (Product product : products) productMap.put(product.getId(), product);

        PagedListDto userOrdersDto = new PagedListDto(totalCount, start, count);
        for (Order order : orders) {
            try {
                Product product = productMap.get(order.getProductId());
                if (product == null) continue;

                userOrdersDto.add(buildOrderDetailDto(order, product));
            } catch (Exception e) {
                LOGGER.error("fail to build order dto for order: {}", order.getId(), e);
            }
        }

        return userOrdersDto;
    }

    private OrderDto buildOrderDetailDto(Order order, Product product) {
        OrderDto orderDto = buildOrderDto(order);
        orderDto.setCover(product.getCover());
        orderDto.setTitle(product.getTitle());
        orderDto.setScheduler(product.getScheduler());
        orderDto.setRegion(MetaUtil.getRegionName(product.getSkuRegionId(order.getSkuId())));
        orderDto.setAddress(product.getSkuAddress(order.getSkuId()));
        orderDto.setPrice(product.getPrice());
        orderDto.setTime(product.getSkuTime(order.getSkuId()));
        orderDto.setFinished(product.isSkuFinished(order.getSkuId()));
        orderDto.setClosed(product.isSkuClosed(order.getSkuId()));

        return orderDto;
    }

    @RequestMapping(value = "/{id}/check", method = RequestMethod.GET)
    public MomiaHttpResponse check(@RequestParam String utoken,
                                   @PathVariable(value = "id") long id,
                                   @RequestParam(value = "pid") long productId,
                                   @RequestParam(value = "sid") long skuId) {
        UserDto user = UserServiceApi.USER.get(utoken);
        Order order = orderService.get(id);
        if (!order.isPayed() ||
                order.getCustomerId() != user.getId() ||
                order.getProductId() != productId ||
                order.getSkuId() != skuId) return MomiaHttpResponse.SUCCESS(false);

        return MomiaHttpResponse.SUCCESS(true);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse detail(@RequestParam String utoken,
                                    @PathVariable(value = "id") long id,
                                    @RequestParam(value = "pid") long productId) {
        UserDto user = UserServiceApi.USER.get(utoken);
        Order order = orderService.get(id);
        Product product = ProductServiceApi.PRODUCT.get(productId, Product.Type.BASE_WITH_SKU);
        if (!order.exists() || !product.exists() ||
                order.getCustomerId() != user.getId() ||
                order.getProductId() != product.getId()) return MomiaHttpResponse.FAILED("无效的订单");

        return MomiaHttpResponse.SUCCESS(buildOrderDetailDto(order, product));
    }

    @RequestMapping(value = "/customer", method = RequestMethod.GET)
    public MomiaHttpResponse getProductCustomersInfo(@RequestParam(value = "pid") long productId, @RequestParam int count) {
        if (productId <= 0 || count <= 0) return MomiaHttpResponse.BAD_REQUEST;

        List<Order> orders = orderService.queryDistinctCustomerOrderByProduct(productId, 0, count);
        if (orders.isEmpty()) return MomiaHttpResponse.SUCCESS(ListDto.EMPTY);

        List<Long> customerIds = new ArrayList<Long>();
        for (Order order : orders) customerIds.add(order.getCustomerId());
        List<UserDto> customers = UserServiceApi.USER.list(customerIds, UserDto.Type.MINI);
        Map<Long, UserDto> customersMap = new HashMap<Long, UserDto>();
        for (UserDto customer : customers) {
            customersMap.put(customer.getId(), customer);
        }

        ListDto avatars = new ListDto();
        for (Order order : orders) {
            long customerId = order.getCustomerId();
            UserDto customer = customersMap.get(customerId);
            if (customer == null) continue;

            avatars.add(customer.getAvatar());
        }

        return MomiaHttpResponse.SUCCESS(avatars);
    }

    @RequestMapping(value = "/playmate", method = RequestMethod.GET)
    public MomiaHttpResponse listPlaymates(@RequestParam(value = "pid") long productId, @RequestParam int start, @RequestParam int count) {
        if (productId <= 0 || isInvalidLimit(start, count)) return MomiaHttpResponse.BAD_REQUEST;

        List<Sku> skus = querySkus(productId, start, count);
        if (skus.isEmpty()) return MomiaHttpResponse.EMPTY_ARRAY;

        List<Order> orders = queryOrders(productId, skus);
        if (orders.isEmpty()) return MomiaHttpResponse.EMPTY_ARRAY;

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

        Map<Long, UserDto> customersMap = new HashMap<Long, UserDto>();
        for (UserDto customer : UserServiceApi.USER.list(customerIds, UserDto.Type.FULL)) {
            customersMap.put(customer.getId(), customer);
        }

        return MomiaHttpResponse.SUCCESS(buildPlaymates(skus, skuOrdersMap, skuCustomerIdsMap, customersMap));
    }

    private List<Sku> querySkus(long id, int start, int count) {
        List<Sku> skus = ProductServiceApi.SKU.list(id, Sku.Status.ALL);
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
        List<Order> orders = orderService.queryAllCustomerOrderByProduct(id);
        for (Order order : orders) {
            if (skuIds.contains(order.getSkuId())) result.add(order);
        }

        return result;
    }

    private List<SkuPlaymatesDto> buildPlaymates(List<Sku> skus,
                                                 Map<Long, List<Order>> skuOrdersMap,
                                                 Map<Long, List<Long>> skuCustomerIdsMap,
                                                 Map<Long, UserDto> customersMap) {
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

    private List<PlaymateDto> extractPlayMates(long skuId, Map<Long, List<Long>> skuCustomerIdsMap, Map<Long, UserDto> customersMap) {
        int pageSize = Configuration.getInt("Product.Playmate.PageSize");
        List<PlaymateDto> playmatesDto = new ArrayList<PlaymateDto>();
        List<Long> customerIds = skuCustomerIdsMap.get(skuId);
        if (customerIds != null) {
            for (long customerId : customerIds) {
                PlaymateDto playmateDto = new PlaymateDto();
                UserDto customer = customersMap.get(customerId);
                if (customer == null) continue;
                playmateDto.setId(customer.getId());
                playmateDto.setNickName(customer.getNickName());
                playmateDto.setAvatar(customer.getAvatar());

                List<String> childrenStrs = new ArrayList<String>();
                ListDto children = customer.getChildren();
                if (children != null) {
                    for (Object childObj : children) {
                        ParticipantDto child = (ParticipantDto) childObj;
                        childrenStrs.add(child.getSex() + "孩" + TimeUtil.formatAge(child.getBirthday()));
                    }
                }
                playmateDto.setChildren(childrenStrs);

                if (playmatesDto.size() < pageSize) playmatesDto.add(playmateDto);
                else break;
            }
        }

        return playmatesDto;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public MomiaHttpResponse queryUsersByProduct(@RequestParam(value = "pid") long productId, @RequestParam(value = "sid") long skuId) {
        return MomiaHttpResponse.SUCCESS(orderService.queryUserIds(productId, skuId));
    }
}
