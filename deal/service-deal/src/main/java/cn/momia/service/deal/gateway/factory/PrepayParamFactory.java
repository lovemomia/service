package cn.momia.service.deal.gateway.factory;

import cn.momia.api.base.exception.MomiaFailedException;
import cn.momia.service.deal.gateway.PrepayParam;
import cn.momia.service.deal.gateway.alipay.AlipayPrepayParam;
import cn.momia.service.deal.gateway.wechatpay.WechatpayPrepayParam;
import cn.momia.service.deal.payment.Payment;

import java.util.Map;

public class PrepayParamFactory {
    public static PrepayParam create(Map<String, String> params, int payType) {
        switch (payType) {
            case Payment.Type.ALIPAY: return new AlipayPrepayParam(params);
            case Payment.Type.WECHATPAY: return new WechatpayPrepayParam(params);
            default: throw new MomiaFailedException("无效的支付类型: " + payType);
        }
    }
}
