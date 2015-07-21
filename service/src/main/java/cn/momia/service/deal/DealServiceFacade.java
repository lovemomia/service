package cn.momia.service.deal;

import cn.momia.service.deal.exception.OrderLimitException;
import cn.momia.service.deal.order.Order;

import java.util.List;
import java.util.Map;

public interface DealServiceFacade {
    long placeOrder(Order order);
    void checkLimit(long userId, long skuId, int count, int limit) throws OrderLimitException;

    Order getOrder(long orderId);
    boolean deleteOrder(long userId, long orderId);
    long queryOrderCountByUser(long userId, int status);
    List<Order> queryOrderByUser(long userId, int status, int start, int count);

    boolean prepayOrder(long orderId);
    boolean payOrder(long orderId);

    boolean callback(Map<String, String> params, int payType);
    boolean check(long userId, long orderId, long productId, long skuId);
}
