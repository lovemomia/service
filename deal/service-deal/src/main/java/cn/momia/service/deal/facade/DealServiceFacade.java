package cn.momia.service.deal.facade;

import cn.momia.service.deal.exception.OrderLimitException;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;

import java.util.List;

public interface DealServiceFacade {
    long placeOrder(Order order);
    void checkLimit(long userId, long skuId, int count, int limit) throws OrderLimitException;

    Order getOrder(long orderId);
    List<Order> getOrders(long userId, long productId, long skuId);
    boolean deleteOrder(long userId, long orderId);
    long queryOrderCountByUser(long userId, int status);
    List<Order> queryOrderByUser(long userId, int status, int start, int count);

    List<Order> queryDistinctCustomerOrderByProduct(long productId, int start, int count);
    List<Order> queryAllCustomerOrderByProduct(long productId);

    boolean prepayOrder(long orderId);
    boolean payOrder(long orderId);

    boolean check(long userId, long orderId, long productId, long skuId);
    boolean logPayment(Payment payment);
}
