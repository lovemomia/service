package cn.momia.service.deal.gateway;

public abstract class PaymentGateway {
    public abstract PrepayResult prepay(PrepayParam param);

    public CallbackResult callback(CallbackParam param) {
        CallbackResult result = new CallbackResult();
        result.setOrderId(param.getOrderId());

        if (param.isPayedSuccessfully()) {
            result.setSuccessful(true);
            result.setPayType(param.getPayType());
            result.setPayer(param.getPayer());
            result.setFinishTime(param.getFinishTime());
            result.setTradeNo(param.getTradeNo());
            result.setTotalFee(param.getTotalFee());
        }

        return result;
    }
}