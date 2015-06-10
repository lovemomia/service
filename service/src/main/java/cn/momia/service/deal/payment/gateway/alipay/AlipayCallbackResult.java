package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.service.deal.payment.gateway.CallbackResult;

public class AlipayCallbackResult implements CallbackResult {
    @Override
    public boolean isSuccessful() {
        return false;
    }
}
