package cn.momia.service.deal.payment;

import cn.momia.service.base.Service;

public interface PaymentService extends Service {
    long add(Payment payment);
    Payment get(long id);
    Payment getByOrder(long orderId);
}
