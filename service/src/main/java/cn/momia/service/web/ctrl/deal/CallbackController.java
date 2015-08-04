package cn.momia.service.web.ctrl.deal;

import cn.momia.service.web.response.ResponseMessage;
import cn.momia.service.deal.gateway.CallbackResult;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.web.ctrl.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/callback")
public class CallbackController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackController.class);

    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public ResponseMessage alipayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.ALIPAY);
    }

    private ResponseMessage callback(HttpServletRequest request, int payType) {
        CallbackResult result = dealServiceFacade.callback(extractParams(request.getParameterMap()), payType);
        if (result.isSuccessful()) {
            long orderId = result.getOrderId();
            Order order = dealServiceFacade.getOrder(orderId);
            if (order.exists()) {
                updateUserCoupon(order);
                updateSales(order);
            }

            return ResponseMessage.SUCCESS;
        }

        LOGGER.error("fail to finish payment for order: {}", result.getOrderId());

        return ResponseMessage.FAILED;
    }

    private void updateUserCoupon(Order order) {
        try {
            UserCoupon userCoupon = promoServiceFacade.getNotUsedUserCouponByOrder(order.getId());
            if (!promoServiceFacade.useUserCoupon(order.getCustomerId(), order.getId(), userCoupon.getId()))
                LOGGER.error("fail to update user coupon of order: {}", order.getId());
        } catch (Exception e) {
            LOGGER.error("fail to update user coupon of order: {}", order.getId(), e);
        }
    }

    private void updateSales(Order order) {
        try {
            if (!productServiceFacade.sold(order.getProductId(), order.getCount())) {
                LOGGER.error("fail to update sales of order: {}", order.getId());
            }
        } catch (Exception e) {
            LOGGER.error("fail to update sales of order: {}", order.getId(), e);
        }
    }

    @RequestMapping(value = "/wechatpay", method = RequestMethod.POST)
    public ResponseMessage wechatpayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.WECHATPAY);
    }
}
