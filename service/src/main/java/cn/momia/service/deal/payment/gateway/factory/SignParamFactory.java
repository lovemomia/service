package cn.momia.service.deal.payment.gateway.factory;

import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.SignParam;
import cn.momia.service.deal.payment.gateway.alipay.AlipaySignParam;
import cn.momia.service.deal.payment.gateway.wechatpay.WechatpaySignParam;

import java.util.Map;

public class SignParamFactory {
    public static SignParam create(Map<String, String[]> httpParams, int payType) {
        SignParam signParam = createSignParam(payType);

        for (Map.Entry<String, String[]> entry : httpParams.entrySet()) {
            String[] values = entry.getValue();
            if (values.length <= 0) continue;
            signParam.add(entry.getKey(), entry.getValue()[0]);
        }

        return signParam;
    }

    private static SignParam createSignParam(int payType) {
        switch (payType) {
            case Payment.Type.ALIPAY:
                return new AlipaySignParam();
            case Payment.Type.WECHATPAY:
                return new WechatpaySignParam();
            default:
                throw new RuntimeException("invalid pay type: " + payType);
        }
    }
}
