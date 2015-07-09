package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.AbstractPaymentGateway;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class AlipayGateway extends AbstractPaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayGateway.class);

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
    protected boolean isPayedSuccessfully(CallbackParam param) {
        // TODO
        return false;
    }

    @Override
    protected boolean validateCallbackSign(CallbackParam param) {
        boolean successful = AlipayUtil.validateSign(param.getAll());
        if (!successful) LOGGER.warn("invalid sign, order id: {} ", param.get(AlipayCallbackFields.OUT_TRADE_NO));

        return successful;
    }

    @Override
    protected Payment createPayment(CallbackParam param) {
        // TODO
        return null;
    }

    @Override
    protected CallbackResult buildFailResult() {
        CallbackResult result = new CallbackResult();
        result.setSuccessful(false);

        return result;
    }

    @Override
    protected CallbackResult buildSuccessResult() {
        CallbackResult result = new CallbackResult();
        result.setSuccessful(true);

        return result;
    }
}
