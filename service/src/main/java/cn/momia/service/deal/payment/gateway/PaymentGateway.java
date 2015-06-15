package cn.momia.service.deal.payment.gateway;

import cn.momia.common.config.Configuration;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.payment.PaymentService;

public interface PaymentGateway {
    void setConf(Configuration conf);
    void setOrderService(OrderService orderService);
    void setPaymentService(PaymentService paymentService);

    PrepayResult prepay(PrepayParam param);
    CallbackResult callback(CallbackParam param);
}