package cn.momia.service.deal.payment;

public interface PaymentService {
    long add(Payment payment);
    Payment get(long id);
    Payment getByOrder(long orderId);
}
