package cn.momia.service.deal.gateway;

public interface PaymentGateway {
    PrepayResult prepay(PrepayParam param);
    CallbackResult callback(CallbackParam param);
}