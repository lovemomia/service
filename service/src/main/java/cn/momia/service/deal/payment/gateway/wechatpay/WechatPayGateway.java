package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PaymentGateway;

public class WechatPayGateway implements PaymentGateway {
    @Override
    public CallbackResult callback(CallbackParam param) {
        return null;
    }
}
