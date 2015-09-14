package cn.momia.service.deal.order;

import java.util.List;

public interface OrderService {
    long add(Order order);

    Order get(long id);
    List<Order> list(long userId, long productId, long skuId);

    long queryCountByUser(long userId, int status);
    List<Order> queryByUser(long userId, int status, int start, int count);
    List<Order> queryByUserAndSku(long userId, long skuId);
    List<Order> queryAllCustomerOrderByProduct(long productId);
    List<Order> queryDistinctCustomerOrderByProduct(long productId, int start, int count);

    void checkLimit(long userId, long skuId, int count, int limit) throws OrderLimitException;

    boolean delete(long userId, long id);
    boolean prepay(long id);
    boolean pay(long id);
    boolean pay(Payment payment);
    boolean check(long userId, long id, long productId, long skuId);
}
