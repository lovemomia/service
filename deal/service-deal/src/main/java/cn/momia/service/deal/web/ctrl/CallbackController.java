package cn.momia.service.deal.web.ctrl;

import cn.momia.api.common.CommonServiceApi;
import cn.momia.api.product.Product;
import cn.momia.api.product.sku.Sku;
import cn.momia.api.user.UserServiceApi;
import cn.momia.service.base.web.ctrl.AbstractController;
import cn.momia.service.deal.facade.DealServiceFacade;
import cn.momia.service.deal.gateway.CallbackResult;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.api.product.ProductServiceApi;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.base.web.response.ResponseMessage;
import cn.momia.service.promo.facade.PromoServiceFacade;
import org.apache.commons.lang3.StringUtils;
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
            if (order.exists() && order.isPayed() &&
                    (payType == Payment.Type.WECHATPAY ||
                            (payType == Payment.Type.ALIPAY && "TRADE_SUCCESS".equalsIgnoreCase(request.getParameter("trade_status"))))) {
                updateUserCoupon(order);
                updateSales(order);
                notifyUser(order);
                distributeCoupon(order);

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
            ProductServiceApi.PRODUCT.sold(order.getProductId(), order.getCount());
        } catch (Exception e) {
            LOGGER.error("fail to update sales of order: {}", order.getId(), e);
        }
    }

    private void notifyUser(Order order) {
        try {
            Product product = ProductServiceApi.PRODUCT.get(order.getProductId(), Product.Type.BASE_WITH_SKU);
            Sku sku = product.getSku(order.getSkuId());
            if (!sku.exists()) return;

            StringBuilder msg = new StringBuilder();
            msg.append("您的订单：\"")
                    .append(product.getTitle())
                    .append("\"付款成功");

            if (sku.getType() == 1) {
                msg.append("，如果您被选中参加，我们的客服会在活动开始前提前与您联系");
            } else {
                msg.append("，时间：")
                        .append(sku.getTime())
                        .append("，地点：")
                        .append(product.getAddress());
            }

            msg.append("【松果亲子】");
            CommonServiceApi.SMS.notify(order.getMobile(), msg.toString());
        } catch (Exception e) {
            LOGGER.error("fail to notify user for order: {}", order.getId(), e);
        }
    }

    private void distributeCoupon(Order order) {
        try {
            if (!UserServiceApi.USER.isPayed(order.getCustomerId()) &&
                    UserServiceApi.USER.setPayed(order.getCustomerId())) {
                if (StringUtils.isBlank(order.getInviteCode())) return;
                long userId = UserServiceApi.USER.getIdByInviteCode(order.getInviteCode());
                if (userId <= 0) return;

                promoServiceFacade.distributeShareCoupon(order.getCustomerId(), userId, order.getTotalFee());
            }
        } catch (Exception e) {
            LOGGER.error("fail to distribute share coupon for order: {}", order.getId(), e);
        }
    }

    @RequestMapping(value = "/wechatpay", method = RequestMethod.POST)
    public ResponseMessage wechatpayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.WECHATPAY);
    }
}
