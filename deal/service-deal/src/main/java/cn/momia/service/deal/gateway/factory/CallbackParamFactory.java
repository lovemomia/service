package cn.momia.service.deal.gateway.factory;

import cn.momia.api.base.exception.MomiaFailedException;
import cn.momia.service.deal.gateway.CallbackParam;
import cn.momia.service.deal.gateway.alipay.AlipayCallbackParam;
import cn.momia.service.deal.gateway.wechatpay.WechatpayCallbackParam;
import cn.momia.service.deal.payment.Payment;

import java.util.Map;

public class CallbackParamFactory {
    public static CallbackParam create(Map<String, String> params, int payType) {
        switch (payType) {
            case Payment.Type.ALIPAY: return new AlipayCallbackParam(params);
            case Payment.Type.WECHATPAY: return new WechatpayCallbackParam(params);
            default: throw new MomiaFailedException("无效的支付类型: " + payType);
        }
    }
}
