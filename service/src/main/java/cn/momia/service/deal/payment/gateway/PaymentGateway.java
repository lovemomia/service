package cn.momia.service.deal.payment.gateway;

public interface PaymentGateway {
    PrepayResult prepay(PrepayParam param);
    CallbackResult callback(CallbackParam param);
}