package cn.momia.service.deal.order;

import java.util.List;

public interface OrderService {
    boolean checkOrder(Order order);
    long add(Order order);
    Order get(long id);
    List<Order> queryByProduct(long productId, int status, String type, int start, int count);
    long queryCountByUser(long userId, int status, String type);
    List<Order> queryByUserAndSku(long userId, long skuId);
    List<Order> queryByUser(long userId, int status, String type, int start, int count);
    List<Order> queryAllCustomerOrderByProduct(long productId);
    List<Order> queryDistinctCustomerOrderByProduct(long productId, int start, int count);
    boolean delete(long id, long userId);
    boolean pay(long id);
    boolean check(long id, long userId, long productId, long skuId);
}
