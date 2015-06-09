package cn.momia.service.deal.payment.gateway.unionpay;

import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PaymentGateway;

public class UnionpayGateway implements PaymentGateway {
    @Override
    public CallbackResult callback(CallbackParam param) {
        return null;
    }
}
