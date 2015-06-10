package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.common.sign.Signer;
import cn.momia.common.web.secret.SecretKey;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.SignParam;

public class AlipayGateway implements PaymentGateway {
    @Override
    public String sign(SignParam param) {
        String content = param.toString();

        return Signer.sign(param.get("sign_type"), content, SecretKey.getAlipayPrivateKey());
    }

    @Override
    public CallbackResult callback(CallbackParam param) {
        return null;
    }
}
