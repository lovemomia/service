package cn.momia.service.deal.gateway.alipay;

import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.deal.gateway.AbstractPaymentGateway;
import cn.momia.service.deal.gateway.PrepayResult;
import cn.momia.service.deal.gateway.ClientType;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.gateway.PrepayParam;
import org.apache.commons.lang3.StringUtils;

public class AlipayGateway extends AbstractPaymentGateway {
    @Override
    public PrepayResult doPrepay(PrepayParam param) {
        AlipayPrepayParam alipayPrepayParam = (AlipayPrepayParam) param;
        PrepayResult result = new AlipayPrepayResult();

        if (ClientType.isFromApp(param.getClientType())) {
            result.add(AlipayPrepayResult.Field.SERVICE, Configuration.getString("Payment.Ali.AppService"));
        } else if (ClientType.isFromWap(param.getClientType())) {
            result.add(AlipayPrepayResult.Field.SERVICE, Configuration.getString("Payment.Ali.WapService"));
            result.add(AlipayPrepayResult.Field.RETURN_URL, alipayPrepayParam.getProductUrl());
        }

        result.add(AlipayPrepayResult.Field.PARTNER, Configuration.getString("Payment.Ali.Partner"));
        result.add(AlipayPrepayResult.Field.INPUT_CHARSET, "utf-8");
        result.add(AlipayPrepayResult.Field.SIGN_TYPE, "RSA");
        result.add(AlipayPrepayResult.Field.NOTIFY_URL, Configuration.getString("Payment.Ali.NotifyUrl"));
        result.add(AlipayPrepayResult.Field.OUT_TRADE_NO, alipayPrepayParam.getOutTradeNo());
        result.add(AlipayPrepayResult.Field.SUBJECT, alipayPrepayParam.getProductTitle());
        result.add(AlipayPrepayResult.Field.PAYMENT_TYPE, "1");
        result.add(AlipayPrepayResult.Field.SELLER_ID, Configuration.getString("Payment.Ali.Partner"));
        result.add(AlipayPrepayResult.Field.TOTAL_FEE, alipayPrepayParam.getTotalFee());
        result.add(AlipayPrepayResult.Field.BODY, alipayPrepayParam.getProductTitle());
        result.add(AlipayPrepayResult.Field.IT_B_PAY, "30m");
        result.add(AlipayPrepayResult.Field.SHOW_URL, Configuration.getString("Wap.Domain"));
        result.add(AlipayPrepayResult.Field.SIGN, AlipayUtil.sign(result.getAll(), param.getClientType()));

        result.setSuccessful(!StringUtils.isBlank(result.get(AlipayPrepayResult.Field.SIGN)));

        return result;
    }

    @Override
    protected int getPayType() {
        return Payment.Type.ALIPAY;
    }
}
