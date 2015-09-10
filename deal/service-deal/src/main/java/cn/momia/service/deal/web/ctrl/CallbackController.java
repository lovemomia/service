package cn.momia.service.deal.web.ctrl;

import cn.momia.api.base.BaseServiceApi;
import cn.momia.api.product.Product;
import cn.momia.api.product.sku.Sku;
import cn.momia.api.user.UserServiceApi;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.deal.gateway.CallbackParam;
import cn.momia.service.deal.gateway.CallbackResult;
import cn.momia.service.deal.gateway.PaymentGateway;
import cn.momia.service.deal.gateway.factory.CallbackParamFactory;
import cn.momia.service.deal.gateway.factory.PaymentGatewayFactory;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.order.Payment;
import cn.momia.api.product.ProductServiceApi;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.promo.facade.PromoServiceFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/callback")
public class CallbackController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackController.class);

    @Autowired private OrderService orderService;
    @Autowired private PromoServiceFacade promoServiceFacade;

    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public MomiaHttpResponse alipayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.ALIPAY);
    }

    private MomiaHttpResponse callback(HttpServletRequest request, int payType) {
        CallbackParam callbackParam = CallbackParamFactory.create(extractParams(request), payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        CallbackResult result = gateway.callback(callbackParam);

        if (!result.isSuccessful()) return MomiaHttpResponse.SUCCESS("OK");

        long orderId = result.getOrderId();
        Order order = orderService.get(orderId);
        if (!order.exists()) {
            // TODO 自动退款
            return MomiaHttpResponse.SUCCESS("OK");
        }

        if (order.isPayed()) {
            // TODO 判断是否重复付款，是则退款
            return MomiaHttpResponse.SUCCESS("OK");
        }

        if (!finishPayment(order, result)) return MomiaHttpResponse.SUCCESS("FAIL");

        return MomiaHttpResponse.SUCCESS("OK");
    }

    private boolean finishPayment(Order order, CallbackResult result) {
        if (!orderService.pay(createPayment(result))) return false;

        updateUserCoupon(order);
        updateSales(order);
        notifyUser(order);
        distributeCoupon(order);

        return true;
    }

    private Payment createPayment(CallbackResult result) {
        Payment payment = new Payment();
        payment.setOrderId(result.getOrderId());
        payment.setPayer(result.getPayer());
        payment.setFinishTime(result.getFinishTime());
        payment.setPayType(result.getPayType());
        payment.setTradeNo(result.getTradeNo());
        payment.setFee(result.getTotalFee());

        return payment;
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
                msg.append("，参加规则详见活动说明");
            } else {
                msg.append("，时间：")
                        .append(sku.getTime())
                        .append("，地点：");
                String address = sku.getAddress();
                if (StringUtils.isBlank(address)) address = product.getAddress();
                msg.append(address);
            }

            msg.append("【松果亲子】");
            BaseServiceApi.SMS.notify(order.getMobile(), msg.toString());
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
                if (userId <= 0 || userId == order.getCustomerId()) return;

                promoServiceFacade.distributeShareCoupon(order.getCustomerId(), userId, order.getTotalFee());
            }
        } catch (Exception e) {
            LOGGER.error("fail to distribute share coupon for order: {}", order.getId(), e);
        }
    }

    @RequestMapping(value = "/wechatpay", method = RequestMethod.POST)
    public MomiaHttpResponse wechatpayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.WECHATPAY);
    }
}
