package cn.momia.service.course.subject.order;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OrderService {
    long add(Order order);
    Order get(long orderId);
    List<Order> list(Collection<Long> orderIds);

    long queryCountByUser(long userId, int status);
    List<Order> queryByUser(long userId, int status, int start, int count);

    long queryBookableCountByUser(long userId);
    List<OrderPackage> queryBookableByUser(long userId, int start, int count);

    Map<Long, Date> queryStartTimesByPackages(Set<Long> packageIds);

    boolean prepay(long orderId);
    boolean pay(Payment payment);
}
