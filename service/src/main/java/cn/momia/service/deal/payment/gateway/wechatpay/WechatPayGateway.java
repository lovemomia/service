package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;
import cn.momia.service.deal.payment.gateway.SignParam;

public class WechatPayGateway implements PaymentGateway {
    @Override
    public String sign(SignParam param) {
        return null;
    }

    @Override
    public PrepayResult prepay(PrepayParam param) {
        // TODO
        return null;
    }

    @Override
    public CallbackResult callback(CallbackParam param) {
        return null;
    }
}
