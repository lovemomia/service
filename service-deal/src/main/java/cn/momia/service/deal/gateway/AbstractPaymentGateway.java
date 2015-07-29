package cn.momia.service.deal.gateway;

import cn.momia.common.config.Configuration;
import cn.momia.service.deal.DealServiceFacade;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPaymentGateway implements PaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentGateway.class);

    protected Configuration conf;
    protected DealServiceFacade dealServiceFacade;

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public void setDealServiceFacade(DealServiceFacade dealServiceFacade) {
        this.dealServiceFacade = dealServiceFacade;
    }

    @Override
    public PrepayResult prepay(PrepayParam param) {
        if (!dealServiceFacade.prepayOrder(getPrepayOutTradeNo(param))) return buildFailPrepayResult();
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
    public CallbackResult callback(CallbackParam param) {
        CallbackResult result = new CallbackResult();
        result.add("orderId", String.valueOf(getCallbackOutTradeNo(param)));

        if (isPayedSuccessfully(param) && validateCallbackSign(param) && !finishPayment(param)) {
            result.setSuccessful(false);
        } else {
            result.setSuccessful(true);
        }

        return result;
    }

    protected abstract boolean isPayedSuccessfully(CallbackParam param);

    protected abstract boolean validateCallbackSign(CallbackParam param);

    private boolean finishPayment(CallbackParam param) {
        long orderId = getCallbackOutTradeNo(param);
        try {
            Order order = dealServiceFacade.getOrder(orderId);
            // 这段逻辑返回true看似有点反常
            // 因为当订单无效或payOrder返回false(代表之前已经完成过付款)时
            // 如果返回false，第三方支付系统会认为通知失败进而多次尝试通知，而实际是不需要的
            // 只有写入数据库时抛了异常才是需要重试的
            if (!order.exists() || !dealServiceFacade.payOrder(orderId)) return true;

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
            if (!dealServiceFacade.logPayment(createPayment(param)))
                LOGGER.error("fail to log payment: {}", param);
        } catch (Exception e) {
            LOGGER.error("fail to log payment: {}", param, e);
        }
    }

    protected abstract Payment createPayment(CallbackParam param);
}
