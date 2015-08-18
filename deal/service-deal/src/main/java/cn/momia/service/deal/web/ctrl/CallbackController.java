package cn.momia.service.deal.web.ctrl;

import cn.momia.api.common.CommonServiceApi;
import cn.momia.api.product.Product;
import cn.momia.service.base.web.ctrl.AbstractController;
import cn.momia.service.deal.facade.DealServiceFacade;
import cn.momia.service.deal.gateway.CallbackResult;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.api.product.ProductServiceApi;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.base.web.response.ResponseMessage;
import cn.momia.service.promo.facade.PromoServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/callback")
public class CallbackController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackController.class);

    @Autowired private DealServiceFacade dealServiceFacade;
    @Autowired private PromoServiceFacade promoServiceFacade;

    @Autowired private CommonServiceApi commonServiceApi;
    @Autowired private ProductServiceApi productServiceApi;

    private static Map<String, String> extractParams(Map<String, String[]> httpParams) {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> entry : httpParams.entrySet()) {
            String[] values = entry.getValue();
            if (values.length <= 0) continue;
            params.put(entry.getKey(), entry.getValue()[0]);
        }

        return params;
    }

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
                notifyUser(order);
            }

            return ResponseMessage.SUCCESS("OK");
        }

        LOGGER.error("fail to finish payment for order: {}", result.getOrderId());

        return ResponseMessage.SUCCESS("FAIL");
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
            productServiceApi.PRODUCT.sold(order.getProductId(), order.getCount());
        } catch (Exception e) {
            LOGGER.error("fail to update sales of order: {}", order.getId(), e);
        }
    }

    private void notifyUser(Order order) {
        try {
            Product product = productServiceApi.PRODUCT.get(order.getProductId(), Product.Type.BASE_WITH_SKU);
            StringBuilder msg = new StringBuilder();
            msg.append("您的订单：\"")
                    .append(product.getTitle())
                    .append("\"支付成功，时间：")
                    .append(product.getSkuTime(order.getSkuId()))
                    .append("，地点：")
                    .append(product.getAddress())
                    .append("，请准时参加【哆啦亲子】");
            commonServiceApi.SMS.notify(order.getMobile(), msg.toString());
        } catch (Exception e) {
            LOGGER.error("fail to notify user for order: {}", order.getId(), e);
        }
    }

    @RequestMapping(value = "/wechatpay", method = RequestMethod.POST)
    public ResponseMessage wechatpayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.WECHATPAY);
    }
}
