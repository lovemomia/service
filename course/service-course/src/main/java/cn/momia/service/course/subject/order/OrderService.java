package cn.momia.service.course.subject.order;

import java.util.List;

public interface OrderService {
    long add(Order order);
    Order get(long id);

    long queryCountByUser(long userId, int status);
    List<Order> queryByUser(long userId, int status, int start, int count);

    boolean prepay(long id);
    boolean pay(Payment payment);
}
