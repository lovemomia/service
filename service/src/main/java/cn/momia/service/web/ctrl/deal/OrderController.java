package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.deal.exception.OrderLimitException;
import cn.momia.service.product.Product;
import cn.momia.service.product.sku.Sku;
import cn.momia.service.product.sku.SkuPrice;
import cn.momia.service.user.base.User;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderPrice;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage placeOrder(@RequestBody Order order) {
        if (!lockSku(order)) return ResponseMessage.FAILED("库存不足");

        long orderId = 0;
        try {
            if (!checkOrder(order)) return ResponseMessage.FAILED("无效的订单");

            processContacts(order.getCustomerId(), order.getContacts(), order.getMobile());
            checkLimit(order.getCustomerId(), order.getSkuId(), order.getCount());
            increaseJoined(order.getProductId(), order.getCount());

            orderId = dealServiceFacade.placeOrder(order);
            if (orderId > 0) {
                order.setId(orderId);
                JSONObject orderPackJson = new JSONObject();
                orderPackJson.put("order", order);

                return new ResponseMessage(orderPackJson);
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

    private boolean lockSku(Order order) {
        return productServiceFacade.lockStock(order.getProductId(), order.getSkuId(), order.getCount());
    }

    private boolean checkOrder(Order order) {
        if (order.getCustomerId() <= 0 ||
                order.getProductId() <= 0 ||
                order.getSkuId() <= 0 ||
                order.getPrices().isEmpty() ||
                StringUtils.isBlank(order.getMobile())) return false;

        Sku sku = productServiceFacade.getSku(order.getSkuId());
        for (OrderPrice price : order.getPrices()) {
            boolean found = false;
            List<SkuPrice> skuPrices = sku.getPrice(price.getAdult(), price.getChild());
            for (SkuPrice skuPrice : skuPrices) {
                if (price.getPrice().compareTo(skuPrice.getPrice()) == 0) {
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        return true;
    }

    private void processContacts(long customerId, String contacts, String mobile) {
        try {
            if (StringUtils.isBlank(contacts)) return;
            User user = userServiceFacade.getUserByMobile(mobile);
            if (!user.exists()) return;

            if (user.getId() == customerId && StringUtils.isBlank(user.getName()) && !contacts.equals(user.getNickName()))
                userServiceFacade.updateUserName(user.getId(), contacts);
        } catch (Exception e) {
            LOGGER.warn("fail to process contacts, {}/{}", contacts, mobile);
        }
    }

    private void checkLimit(long customerId, long skuId, int count) throws OrderLimitException {
        Sku sku = productServiceFacade.getSku(skuId);

        int limit = sku.getLimit();
        if (limit <= 0) return;

        dealServiceFacade.checkLimit(customerId, skuId, count, limit);
    }

    private void increaseJoined(long productId, int count) {
        try {
            if (!productServiceFacade.join(productId, count)) LOGGER.warn("fail to increase joined of product: {}", productId);
        } catch (Exception e) {
            LOGGER.warn("fail to increase joined of product: {}", productId);
        }
    }

    private boolean unlockSku(Order order) {
        return productServiceFacade.unlockStock(order.getProductId(), order.getSkuId(), order.getCount());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage deleteOrder(@RequestParam String utoken, @PathVariable long id) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = dealServiceFacade.getOrder(id);
        if (!order.exists() || order.getCustomerId() != user.getId()) return ResponseMessage.FAILED("无效的订单");

        if (!dealServiceFacade.deleteOrder(user.getId(), id)) return ResponseMessage.FAILED("删除订单失败");

        // TODO 需要告警
        if (!unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });

        int status = order.getStatus();
        if (status == Order.Status.PRE_PAYED) releaseCoupon(order);

        return ResponseMessage.SUCCESS;
    }

    private void releaseCoupon(Order order) {
        try {
            if (!promoServiceFacade.releaseUserCoupon(order.getCustomerId(), order.getId()))
                LOGGER.error("fail to release coupon of order: {}", order.getId());
        } catch (Exception e) {
            LOGGER.error("fail to release coupon of order: {}", order.getId(), e);
        }
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken,
                                           @RequestParam int status,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        long totalCount = dealServiceFacade.queryOrderCountByUser(user.getId(), status);
        List<Order> orders = totalCount > 0 ? dealServiceFacade.queryOrderByUser(user.getId(), status, start, count) : new ArrayList<Order>();

        List<Long> productIds = new ArrayList<Long>();
        for (Order order : orders) productIds.add(order.getProductId());
        List<Product> products = productIds.isEmpty() ? new ArrayList<Product>() : productServiceFacade.get(productIds);

        return new ResponseMessage(buildUserOrders(totalCount, orders, products));
    }

    private JSONObject buildUserOrders(long totalCount, List<Order> orders, List<Product> products) {
        Map<Long, Product> productMap = new HashMap<Long, Product>();
        Map<Long, Sku> skuMap = new HashMap<Long, Sku>();
        for (Product product : products) {
            productMap.put(product.getId(), product);
            for (Sku sku : product.getSkus()) {
                skuMap.put(sku.getId(), sku);
            }
        }

        JSONObject ordersPackJson = new JSONObject();
        ordersPackJson.put("totalCount", totalCount);

        JSONArray ordersJson = new JSONArray();
        for (Order order : orders) {
            JSONObject orderJson = new JSONObject();
            orderJson.put("order", order);

            Product product = productMap.get(order.getProductId());
            if (product != null) {
                orderJson.put("cover", product.getCover());
                orderJson.put("title", product.getTitle());
            }

            Sku sku = skuMap.get(order.getSkuId());
            if (sku != null) orderJson.put("time", sku.time());

            ordersJson.add(orderJson);
        }
        ordersPackJson.put("orders", ordersJson);

        return ordersPackJson;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken,
                                           @PathVariable(value = "id") long id,
                                           @RequestParam(value = "pid") long productId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = dealServiceFacade.getOrder(id);
        Product product = productServiceFacade.get(productId);
        if (!order.exists() ||
                !product.exists() ||
                order.getCustomerId() != user.getId() ||
                order.getProductId() != product.getId()) return ResponseMessage.FAILED("无效的订单");

        return new ResponseMessage(buildOrderDetail(order, product));
    }

    private JSONObject buildOrderDetail(Order order, Product product) {
        JSONObject orderDetailJson = new JSONObject();
        orderDetailJson.put("order", order);
        orderDetailJson.put("cover", product.getCover());
        orderDetailJson.put("title", product.getTitle());
        orderDetailJson.put("scheduler", product.getScheduler());
        orderDetailJson.put("address", product.getPlace().getAddress());
        orderDetailJson.put("price", product.getMinPrice());

        return orderDetailJson;
    }
}
