package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.common.config.Configuration;
import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class AlipayGateway implements PaymentGateway {
    private Configuration conf;

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public Map<String, String> extractPrepayParams(HttpServletRequest request, Order order, Product product) {
        Map<String, String> params = new HashMap<String, String>();

        params.put(AlipayPrepayFields.SERVICE, "mobile.securitypay.pay");
        params.put(AlipayPrepayFields.PARTNER, conf.getString("Payment.Ali.Partner"));
        params.put(AlipayPrepayFields.INPUT_CHARSET, "utf-8");
        params.put(AlipayPrepayFields.SIGN_TYPE, "RSA");
        params.put(AlipayPrepayFields.NOTIFY_URL, conf.getString("Payment.Ali.NotifyUrl"));
        params.put(AlipayPrepayFields.OUT_TRADE_NO, String.valueOf(order.getId()));
        params.put(AlipayPrepayFields.SUBJECT, product.getTitle());
        params.put(AlipayPrepayFields.PAYMENT_TYPE, "1");
        params.put(AlipayPrepayFields.SELLER_ID, conf.getString("Payment.Ali.Partner"));
        params.put(AlipayPrepayFields.TOTAL_FEE, String.valueOf(order.getTotalFee().floatValue()));
        params.put(AlipayPrepayFields.BODY, product.getTitle());
        params.put(AlipayPrepayFields.IT_B_PAY, "30m");
        params.put(AlipayPrepayFields.SIGN, AlipayUtil.sign(params));

        return params;
    }

    @Override
    public PrepayResult prepay(PrepayParam param) {
        PrepayResult result = new PrepayResult();
        result.setSuccessful(param.get(AlipayPrepayFields.SIGN) != null);
        if (result.isSuccessful()) result.addAll(param.getAll());

        return result;
    }

    @Override
    public CallbackResult callback(CallbackParam param) {
        // TODO
        return null;
    }
}
