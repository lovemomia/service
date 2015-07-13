package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSONObject;
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

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired private OrderService orderService;
    @Autowired private ProductService productService;
    @Autowired private UserService userService;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage placeOrder(@RequestBody Order order) {
        if (!orderService.checkOrder(order)) return ResponseMessage.BAD_REQUEST;
        if (!lockSku(order)) return ResponseMessage.FAILED("库存不足");

        long orderId = 0;
        try {
            processContacts(order.getCustomerId(), order.getContacts(), order.getMobile());
            checkLimit(order.getCustomerId(), order.getSkuId(), order.getCount());
            increaseJoined(order.getProductId(), order.getCount());

            orderId = orderService.add(order);
            if (orderId > 0) {
                order.setId(orderId);
                JSONObject orderPackJson = new JSONObject();
                orderPackJson.put("order", order);

                return new ResponseMessage(orderPackJson);
            }
        } catch (OrderLimitException e) {
            return ResponseMessage.FAILED("本单有限购，您以超出购买限额，超出数量：" + e.getOverCount());
        } catch (Exception e) {
            LOGGER.error("fail to place order, customerId: {}, productId: {}, skuId: {}", new Object[] { order.getCustomerId(), order.getProductId(), order.getSkuId(), e });
        } finally {
            // TODO 需要告警
            if (orderId <= 0 && !unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });
        }

        return ResponseMessage.FAILED("下单失败");
    }

    private boolean lockSku(Order order) {
        return productService.lockStock(order.getSkuId(), order.getCount());
    }

    private void processContacts(long customerId, String contacts, String mobile) {
        try {
            if (StringUtils.isBlank(contacts)) return;
            User user = userService.getByMobile(mobile);
            if (!user.exists()) return;

            if (user.getId() == customerId && StringUtils.isBlank(user.getName())) userService.updateName(user.getId(), contacts);
        } catch (Exception e) {
            LOGGER.warn("fail to process contacts, {}/{}", contacts, mobile);
        }
    }

    private void checkLimit(long customerId, long skuId, int count) throws OrderLimitException {
        int limit;
        try {
            Sku sku = productService.getSku(skuId);

            limit = sku.getLimit();
            if (limit <= 0) return;

            List<Order> orders = orderService.queryByUserAndSku(customerId, skuId);
            for (Order order : orders) {
                if (!order.exists()) continue;
                count += order.getCount();
            }

            if (count <= limit) return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        throw new OrderLimitException(count - limit);
    }

    private void increaseJoined(long productId, int count) {
        try {
            if (!productService.join(productId, count)) LOGGER.warn("fail to increase joined of product: {}", productId);
        } catch (Exception e) {
            LOGGER.warn("fail to increase joined of product: {}", productId);
        }
    }

    private boolean unlockSku(Order order) {
        return productService.unlockStock(order.getSkuId(), order.getCount());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage deleteOrder(@RequestParam String utoken, @PathVariable long id) {
        if (StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = orderService.get(id);
        if (!order.exists()) return ResponseMessage.BAD_REQUEST;

        if (!orderService.delete(id, user.getId())) return ResponseMessage.FAILED("删除订单失败");

        // TODO 需要告警
        if (!unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });

        return ResponseMessage.SUCCESS;
    }

    private static class OrderLimitException extends Exception {
        private int overCount;

        public int getOverCount() {
            return overCount;
        }

        public OrderLimitException(int overCount) {
            this.overCount = overCount;
        }
    }
}
