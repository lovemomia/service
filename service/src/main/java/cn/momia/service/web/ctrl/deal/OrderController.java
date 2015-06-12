package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.product.sku.SkuService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.payment.PaymentService;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SkuService skuService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage placeOrder(@RequestParam(value = "order") String orderJson) {
        Order order = parseOrder(orderJson);
        if (!lockSku(order)) return new ResponseMessage(ErrorCode.FAILED, "low stocks");

        try {
            long orderId = orderService.add(order);
            if (orderId > 0) {
                order.setId(orderId);
                return new ResponseMessage(order);
            }
        } catch (Exception e) {
            LOGGER.error("fail to place order, customerId: {}, productId: {}, skuId: {}", new Object[] { order.getCustomerId(), order.getProductId(), order.getSkuId(), e });
        }

        if (!unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });

        return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to place order");
    }

    private Order parseOrder(String orderJson) {
        Order order = new Order();

        JSONObject orderObject = JSON.parseObject(orderJson);
        order.setCustomerId(orderObject.getLong("customerId"));
        order.setProductId(orderObject.getLong("productId"));
        order.setSkuId(orderObject.getLong("skuId"));
        order.setPrice(orderObject.getFloat("price"));
        order.setCount(orderObject.getInteger("count"));
        order.setContacts(orderObject.getString("contacts"));
        order.setMobile(orderObject.getString("mobile"));

        List<Long> participants = new ArrayList<Long>();
        JSONArray participantArray = orderObject.getJSONArray("participants");
        for (int i = 0; i < participantArray.size(); i++) {
            participants.add(participantArray.getLong(i));
        }
        order.setParticipants(participants);

        return order;
    }

    private boolean lockSku(Order order) {
        return skuService.lock(order.getSkuId(), order.getCount());
    }

    private boolean unlockSku(Order order) {
        return skuService.unlock(order.getSkuId(), order.getCount());
    }

    @RequestMapping(value = "/{id}/pay", method = RequestMethod.PUT)
    public ResponseMessage payOrder(@PathVariable long id) {
        if (orderService.pay(id)) return new ResponseMessage(paymentService.getByOrder(id));

        LOGGER.error("fail to finish payment of order: {}", id);

        return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to pay order");
    }
}
