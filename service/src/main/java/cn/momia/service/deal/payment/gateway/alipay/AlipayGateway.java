package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.AbstractPaymentGateway;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;
import com.alibaba.fastjson.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AlipayGateway extends AbstractPaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayGateway.class);
    private static final String SUCCESS = "TRADE_SUCCESS";

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
        String trade_status = param.get(AlipayCallbackFields.TRADE_STATUS);
        return trade_status != null && trade_status.equalsIgnoreCase(SUCCESS);
    }

    @Override
    protected boolean validateCallbackSign(CallbackParam param) {
        boolean successful = AlipayUtil.validateSign(param.getAll());
        if (!successful) LOGGER.warn("invalid sign, order id: {} ", param.get(AlipayCallbackFields.OUT_TRADE_NO));

        return successful;
    }

    @Override
    protected Payment createPayment(CallbackParam param) {
        Payment payment = new Payment();
        payment.setOrderId(Long.valueOf(param.get(AlipayCallbackFields.OUT_TRADE_NO)));
        payment.setPayer(param.get(AlipayCallbackFields.BUYER_EMAIL));
        payment.setFinishTime(TypeUtils.castToDate(param.get(AlipayCallbackFields.GMT_PAYMENT)));
        payment.setPayType(Payment.Type.ALIPAY);
        payment.setTradeNo(param.get(AlipayCallbackFields.TRADE_NO));
        payment.setFee(new BigDecimal(param.get(AlipayCallbackFields.TOTAL_FEE)));

        return payment;
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
