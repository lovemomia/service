package cn.momia.service.deal.payment;

import cn.momia.common.service.Service;

public interface PaymentService extends Service {
    long add(Payment payment);
    Payment get(long id);
    Payment getByOrder(long orderId);
}
