package cn.momia.service.deal.impl;

import cn.momia.service.deal.DealServiceFacade;
import cn.momia.service.deal.exception.OrderLimitException;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.PaymentService;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;
import cn.momia.service.deal.payment.gateway.factory.CallbackParamFactory;
import cn.momia.service.deal.payment.gateway.factory.PaymentGatewayFactory;
import cn.momia.service.deal.payment.gateway.factory.PrepayParamFactory;
import cn.momia.service.product.Product;
import cn.momia.service.promo.coupon.Coupon;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DealServiceFacadeImpl implements DealServiceFacade {
    private OrderService orderService;
    private PaymentService paymentService;

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public long placeOrder(Order order) {
        return orderService.add(order);
    }

    @Override
    public void checkLimit(long userId, long skuId, int count, int limit) throws OrderLimitException {
        if (limit <= 0) return;

        List<Order> orders = orderService.queryByUserAndSku(userId, skuId);
        for (Order order : orders) {
            if (!order.exists()) continue;
            count += order.getCount();
        }

        if (count > limit) throw new OrderLimitException();
    }

    @Override
    public Order getOrder(long orderId) {
        if (orderId <= 0) return Order.NOT_EXIST_ORDER;
        return orderService.get(orderId);
    }

    @Override
    public boolean deleteOrder(long userId, long orderId) {
        if (userId <= 0 || orderId <= 0) return false;
        return orderService.delete(userId, orderId);
    }

    @Override
    public long queryOrderCountByUser(long userId, int status) {
        if (userId <= 0 || status <= 0) return 0;
        return orderService.queryCountByUser(userId, status);
    }

    @Override
    public List<Order> queryOrderByUser(long userId, int status, int start, int count) {
        if (userId <= 0 || status <= 0) return new ArrayList<Order>();
        return orderService.queryByUser(userId, status, start, count);
    }

    @Override
    public List<Order> queryDistinctCustomerOrderByProduct(long productId, int start, int count) {
        return orderService.queryDistinctCustomerOrderByProduct(productId, start, count);
    }

    @Override
    public List<Order> queryAllCustomerOrderByProduct(long productId) {
        return orderService.queryAllCustomerOrderByProduct(productId);
    }

    @Override
    public boolean prepayOrder(long orderId) {
        return orderService.prepay(orderId);
    }

    @Override
    public boolean payOrder(long orderId) {
        return orderService.pay(orderId);
    }

    @Override
    public PrepayResult prepay(HttpServletRequest request, Order order, Product product, Coupon coupon, int payType) {
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        Map<String, String> params = gateway.extractPrepayParams(request, order, product, coupon);
        PrepayParam prepayParam = PrepayParamFactory.create(params, payType);

        return gateway.prepay(prepayParam);
    }

    @Override
    public boolean callback(Map<String, String> httpParams, int payType) {
        CallbackParam callbackParam = CallbackParamFactory.create(httpParams, payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);

        return gateway.callback(callbackParam);
    }

    @Override
    public boolean check(long userId, long orderId, long productId, long skuId) {
        if (userId <= 0 || orderId <= 0 || productId <= 0 || skuId <= 0) return false;
        return orderService.check(userId, orderId, productId, skuId);
    }

    @Override
    public boolean logPayment(Payment payment) {
        if (payment == null) return true;
        return paymentService.add(payment) > 0;
    }
}
