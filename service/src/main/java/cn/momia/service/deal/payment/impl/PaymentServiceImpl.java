package cn.momia.service.deal.payment.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.PaymentService;

public class PaymentServiceImpl extends DbAccessService implements PaymentService {
    @Override
    public long add(Payment payment) {
        return 0;
    }

    @Override
    public Payment get(long paymentId) {
        return null;
    }
}
