package cn.momia.service.deal.impl;

import cn.momia.service.deal.DealServiceFacade;
import cn.momia.service.deal.exception.OrderLimitException;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.payment.PaymentService;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.factory.CallbackParamFactory;
import cn.momia.service.deal.payment.gateway.factory.PaymentGatewayFactory;

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
        return orderService.delete(orderId, userId);
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
    public boolean prepayOrder(long orderId) {
        return orderService.prepay(orderId);
    }

    @Override
    public boolean payOrder(long orderId) {
        return orderService.pay(orderId);
    }

    @Override
    public boolean callback(Map<String, String> params, int payType) {
        CallbackParam callbackParam = CallbackParamFactory.create(params, payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);

        return gateway.callback(callbackParam).isSuccessful();
    }

    @Override
    public boolean check(long userId, long orderId, long productId, long skuId) {
        if (userId <= 0 || orderId <= 0 || productId <= 0 || skuId <= 0) return false;
        return orderService.check(orderId, userId, productId, skuId);
    }
}
