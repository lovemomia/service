package cn.momia.service.deal.gateway.alipay;

import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.deal.gateway.CallbackParam;
import cn.momia.service.deal.gateway.CallbackResult;
import cn.momia.service.deal.gateway.PaymentGateway;
import cn.momia.service.deal.gateway.PrepayResult;
import cn.momia.service.deal.gateway.ClientType;
import cn.momia.service.order.product.Payment;
import cn.momia.service.deal.gateway.PrepayParam;
import org.apache.commons.lang3.StringUtils;

public class AlipayGateway implements PaymentGateway {
    @Override
    public PrepayResult prepay(PrepayParam param) {
        PrepayResult result = new AlipayPrepayResult();

        if (ClientType.isFromApp(param.getClientType())) {
            result.add(AlipayPrepayResult.Field.SERVICE, Configuration.getString("Payment.Ali.AppService"));
        } else if (ClientType.isFromWap(param.getClientType())) {
            result.add(AlipayPrepayResult.Field.SERVICE, Configuration.getString("Payment.Ali.WapService"));
            result.add(AlipayPrepayResult.Field.RETURN_URL, param.getProductUrl());
        }

        result.add(AlipayPrepayResult.Field.PARTNER, Configuration.getString("Payment.Ali.Partner"));
        result.add(AlipayPrepayResult.Field.INPUT_CHARSET, "utf-8");
        result.add(AlipayPrepayResult.Field.SIGN_TYPE, "RSA");
        result.add(AlipayPrepayResult.Field.NOTIFY_URL, Configuration.getString("Payment.Ali.NotifyUrl"));
        result.add(AlipayPrepayResult.Field.OUT_TRADE_NO, String.valueOf(param.getOrderId()));
        result.add(AlipayPrepayResult.Field.SUBJECT, param.getProductTitle());
        result.add(AlipayPrepayResult.Field.PAYMENT_TYPE, "1");
        result.add(AlipayPrepayResult.Field.SELLER_ID, Configuration.getString("Payment.Ali.Partner"));
        result.add(AlipayPrepayResult.Field.TOTAL_FEE, String.valueOf(param.getTotalFee()));
        result.add(AlipayPrepayResult.Field.BODY, param.getProductTitle());
        result.add(AlipayPrepayResult.Field.IT_B_PAY, "30m");
        result.add(AlipayPrepayResult.Field.SHOW_URL, Configuration.getString("Wap.Domain"));
        result.add(AlipayPrepayResult.Field.SIGN, AlipayUtil.sign(result.getAll(), param.getClientType()));

        result.setSuccessful(!StringUtils.isBlank(result.get(AlipayPrepayResult.Field.SIGN)));

        return result;
    }

    @Override
    public CallbackResult callback(CallbackParam param) {
        CallbackResult result = new CallbackResult();
        result.setOrderId(param.getOrderId());
        result.setPayType(Payment.Type.ALIPAY);

        if (param.isPayedSuccessfully()) {
            result.setSuccessful(true);
            result.setPayer(param.getPayer());
            result.setFinishTime(param.getFinishTime());
            result.setTradeNo(param.getTradeNo());
            result.setTotalFee(param.getTotalFee());
        }

        return result;
    }
}
