package cn.momia.service.deal.payment.gateway.factory;

import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.alipay.AlipayGateway;
import cn.momia.service.deal.payment.gateway.wechatpay.WechatPayGateway;

public class PaymentGatewayFactory {
    public static PaymentGateway create(int payType) {
        switch (payType) {
            case Payment.Type.ALIPAY:
                return new AlipayGateway();
            case Payment.Type.WECHATPAY:
                return new WechatPayGateway();
            default:
                throw new RuntimeException("invalid pay type: " + payType);
        }
    }
}
