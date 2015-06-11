package cn.momia.service.deal.payment.gateway;

public interface PaymentGateway {
    String sign(SignParam param);
    PrepayResult prepay(PrepayParam param);
    CallbackResult callback(CallbackParam param);
}
