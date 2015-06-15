package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;

public class AlipayGateway implements PaymentGateway {
    @Override
    public PrepayResult prepay(PrepayParam param) {
        // TODO
        return null;
    }

    @Override
    public CallbackResult callback(CallbackParam param) {
        // TODO
        return null;
    }
}
