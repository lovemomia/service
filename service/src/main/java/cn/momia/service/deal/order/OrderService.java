package cn.momia.service.deal.order;

import java.util.List;

public interface OrderService {
    long add(Order order);
    Order get(long id);
    List<Order> queryByProduct(long productId, int start, int count);
    List<Order> queryByUser(long userId, int start, int count);
    List<Order> queryDistinctCustomerOrderByProduct(long productId, int start, int count);
    boolean delete(long id, long userId);
    boolean prepay(long id);
    boolean pay(long id);
    boolean check(long userId, long productId, long skuId);
}
