package cn.momia.service.deal.order;

import java.util.List;

public interface OrderService {
    long add(Order order);
    Order get(long id);
    List<Order> queryByProduct(long productId, int start, int count);
}
