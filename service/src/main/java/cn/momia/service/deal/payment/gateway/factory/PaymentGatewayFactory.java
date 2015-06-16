package cn.momia.service.deal.payment.gateway.factory;

import cn.momia.common.config.Configuration;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.PaymentService;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.alipay.AlipayGateway;
import cn.momia.service.deal.payment.gateway.wechatpay.WechatpayGateway;

public class PaymentGatewayFactory {
    private static Configuration conf;
    private static OrderService orderService;
    private static PaymentService paymentService;

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public static PaymentGateway create(int payType) {
        PaymentGateway paymentGateway;
        switch (payType) {
            case Payment.Type.ALIPAY:
                paymentGateway = new AlipayGateway();
                break;
            case Payment.Type.WECHATPAY:
                paymentGateway = new WechatpayGateway();
                break;
            default:
                throw new RuntimeException("invalid pay type: " + payType);
        }

        paymentGateway.setConf(conf);
        paymentGateway.setOrderService(orderService);
        paymentGateway.setPaymentService(paymentService);

        return paymentGateway;
    }
}
