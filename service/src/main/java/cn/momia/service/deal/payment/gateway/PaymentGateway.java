package cn.momia.service.deal.payment.gateway;

import cn.momia.common.error.SDKRuntimeException;

public interface PaymentGateway {
    String sign(SignParam param);
    PrepayResult prepay(PrepayParam param);
    CallbackResult callback(CallbackParam param);
}