package cn.momia.service.deal.order;

import java.util.List;

public interface OrderService {
    long add(Order order);
    Order get(long id);
    List<Order> query(OrderQuery orderQuery);
    int getOrderCount(OrderQuery orderQuery);
}
