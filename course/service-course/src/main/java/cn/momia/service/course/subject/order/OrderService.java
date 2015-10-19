package cn.momia.service.course.subject.order;

import java.util.Collection;
import java.util.List;

public interface OrderService {
    long add(Order order);
    Order get(long id);
    List<Order> list(Collection<Long> ids);

    long queryCountByUser(long userId, int status);
    List<Order> queryByUser(long userId, int status, int start, int count);

    long queryBookableCountByUser(long userId);
    List<OrderSku> queryBookableByUser(long userId, int start, int count);

    boolean prepay(long id);
    boolean pay(Payment payment);
}
