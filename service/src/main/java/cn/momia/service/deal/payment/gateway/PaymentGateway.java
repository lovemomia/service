package cn.momia.service.deal.payment.gateway;

public interface PaymentGateway {
    String sign(SignParam param);
    CallbackResult callback(CallbackParam param);
}
