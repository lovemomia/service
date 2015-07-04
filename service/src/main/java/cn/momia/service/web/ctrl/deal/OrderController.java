package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.web.ctrl.AbstractController;
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

@RestController
@RequestMapping("/order")
public class OrderController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired private OrderService orderService;
    @Autowired private ProductService productService;
    @Autowired private UserService userService;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage placeOrder(@RequestBody Order order) {
        if (order.isInvalid()) return ResponseMessage.BAD_REQUEST;
        
        processContacts(order.getCustomerId(), order.getContacts(), order.getMobile());
        if (!lockSku(order)) return ResponseMessage.FAILED("库存不足");

        long orderId = 0;
        try {
            orderId = orderService.add(order);
            if (orderId > 0) {
                order.setId(orderId);
                return new ResponseMessage(order);
            }
        } catch (Exception e) {
            LOGGER.error("fail to place order, customerId: {}, productId: {}, skuId: {}", new Object[] { order.getCustomerId(), order.getProductId(), order.getSkuId(), e });
        }

        // TODO 需要告警
        if (orderId <= 0 && !unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });

        return ResponseMessage.FAILED("下单失败");
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

    private boolean lockSku(Order order) {
        return productService.lockStock(order.getSkuId(), order.getCount());
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
}
