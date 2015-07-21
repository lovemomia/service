package cn.momia.service.deal.payment.gateway;

import cn.momia.common.config.Configuration;
import cn.momia.service.product.ProductServiceFacade;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.PaymentService;
import cn.momia.service.deal.payment.gateway.wechatpay.WechatpayCallbackFields;
import cn.momia.service.promo.coupon.CouponService;
import cn.momia.service.promo.coupon.UserCoupon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPaymentGateway implements PaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentGateway.class);

    protected Configuration conf;
    protected OrderService orderService;
    protected PaymentService paymentService;
    protected ProductServiceFacade productServiceFacade;
    protected CouponService couponService;

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void setProductServiceFacade(ProductServiceFacade productServiceFacade) {
        this.productServiceFacade = productServiceFacade;
    }

    public void setCouponService(CouponService couponService) {
        this.couponService = couponService;
    }

    @Override
    public PrepayResult prepay(PrepayParam param) {
        if (!orderService.prepay(getPrepayOutTradeNo(param))) return buildFailPrepayResult();
        return doPrepay(param);
    }

    protected abstract long getPrepayOutTradeNo(PrepayParam param);

    private PrepayResult buildFailPrepayResult() {
        PrepayResult result = new PrepayResult();
        result.setSuccessful(false);

        return result;
    }

    protected abstract PrepayResult doPrepay(PrepayParam param);

    @Override
    public boolean callback(CallbackParam param) {
        if (isPayedSuccessfully(param) && validateCallbackSign(param) && !finishPayment(param)) {
            return false;
        } else {
            return true;
        }
    }

    protected abstract boolean isPayedSuccessfully(CallbackParam param);

    protected abstract boolean validateCallbackSign(CallbackParam param);

    private boolean finishPayment(CallbackParam param) {
        long orderId = getCallbackOutTradeNo(param);
        try {
            Order order = orderService.get(orderId);
            if (!order.exists()) return false;

            if (!orderService.pay(orderId)) return false;

            UserCoupon userCoupon = couponService.getNotUsedUserCouponByOrder(order.getId());
            if (userCoupon.exists() && !couponService.useUserCoupon(order.getCustomerId(), order.getId(), userCoupon.getId())) return false;
            logPayment(param);
        } catch (Exception e) {
            LOGGER.error("fail to pay order: {}", orderId, e);
            return false;
        }

        return true;
    }

    protected abstract long getCallbackOutTradeNo(CallbackParam param);

    private void logPayment(CallbackParam param) {
        try {
            long paymentId = paymentService.add(createPayment(param));
            if (paymentId <= 0) {
                LOGGER.error("fail to log payment: {}", param);
                return;
            }

            Order order = orderService.get(Long.valueOf(param.get(WechatpayCallbackFields.OUT_TRADE_NO)));
            if (!order.exists()) {
                LOGGER.error("invalid order: {}", order.getId());
                return;
            }

            if (!productServiceFacade.sold(order.getProductId(), order.getCount())) {
                LOGGER.error("fail to log sales of order: {}", order.getId());
            }
        } catch (Exception e) {
            LOGGER.error("fail to log payment: {}", param, e);
        }
    }

    protected abstract Payment createPayment(CallbackParam param);
}
