package cn.momia.service.deal.gateway;

import cn.momia.service.deal.facade.DealServiceFacade;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPaymentGateway implements PaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentGateway.class);

    protected DealServiceFacade dealServiceFacade;

    public void setDealServiceFacade(DealServiceFacade dealServiceFacade) {
        this.dealServiceFacade = dealServiceFacade;
    }

    @Override
    public PrepayResult prepay(PrepayParam param) {
        if (!dealServiceFacade.prepayOrder(param.getOrderId())) return PrepayResult.FAILED;
        return doPrepay(param);
    }

    protected abstract PrepayResult doPrepay(PrepayParam param);

    @Override
    public CallbackResult callback(CallbackParam param) {
        CallbackResult result = new CallbackResult();
        result.setOrderId(param.getOrderId());

        if (param.isPayedSuccessfully() && !finishPayment(param)) result.setSuccessful(false);
        else result.setSuccessful(true);

        return result;
    }

    private boolean finishPayment(CallbackParam param) {
        long orderId = param.getOrderId();
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

    private void logPayment(CallbackParam param) {
        try {
            if (!dealServiceFacade.logPayment(createPayment(param)))
                LOGGER.error("fail to log payment: {}", param);
        } catch (Exception e) {
            LOGGER.error("fail to log payment: {}", param, e);
        }
    }

    private Payment createPayment(CallbackParam param) {
        Payment payment = new Payment();
        payment.setOrderId(param.getOrderId());
        payment.setPayer(param.getPayer());
        payment.setFinishTime(param.getFinishTime());
        payment.setPayType(getPayType());
        payment.setTradeNo(param.getTradeNo());
        payment.setFee(param.getTotalFee());

        return payment;
    }

    protected abstract int getPayType();
}
