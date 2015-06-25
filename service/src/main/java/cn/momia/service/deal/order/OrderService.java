package cn.momia.service.deal.order;

import java.util.List;

public interface OrderService {
    long add(Order order);
    Order get(long id);
    List<Order> queryByProduct(long productId, int status, int start, int count);
    List<Order> queryByUser(long userId, int status, int start, int count);
    List<Order> queryDistinctCustomerOrderByProduct(long productId, int start, int count);
    boolean delete(long id, long userId);
    boolean prepay(long id, long userId);
    boolean unPrepay(long id, long userId);
    boolean pay(long id);
    boolean check(long userId, long productId, long skuId);
}
