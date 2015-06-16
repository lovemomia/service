package cn.momia.service.deal.payment.gateway;

import cn.momia.common.config.Configuration;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.payment.PaymentService;

public abstract class AbstractPaymentGateway implements PaymentGateway {
    protected Configuration conf;
    protected OrderService orderService;
    protected PaymentService paymentService;

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
