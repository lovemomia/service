package cn.momia.service.deal.order;

import cn.momia.service.base.user.User;

import java.util.List;

public interface OrderService {
    long add(Order order);
    Order get(long id);
    List<Order> queryByProduct(long productId, int start, int count);
    List<User> queryUserByProduct(long productId, int start, int count);
    boolean pay(long id);
}
