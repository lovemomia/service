package cn.momia.service.deal.payment.gateway;

public interface PaymentGateway {
    CallbackResult callback(CallbackParam param);
}
