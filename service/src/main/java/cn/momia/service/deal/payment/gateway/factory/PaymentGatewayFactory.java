package cn.momia.service.deal.payment.gateway.factory;

import cn.momia.service.deal.payment.gateway.PaymentGateway;

import java.util.Map;

public class PaymentGatewayFactory {
    private static Map<Integer, PaymentGateway> prototypes;

    public void setPrototypes(Map<Integer, PaymentGateway> prototypes) {
        PaymentGatewayFactory.prototypes = prototypes;
    }

    public static PaymentGateway create(int payType) {
        PaymentGateway paymentGateway = prototypes.get(payType);
        if (paymentGateway == null) throw new RuntimeException("invalid pay type: " + payType);

        return paymentGateway;
    }
}
