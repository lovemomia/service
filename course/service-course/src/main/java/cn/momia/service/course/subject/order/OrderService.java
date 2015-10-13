package cn.momia.service.course.subject.order;

public interface OrderService {
    long add(Order order);
    Order get(long id);

    boolean prepay(long id);
    boolean pay(Payment payment);
}
