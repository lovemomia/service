package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.AbstractPaymentGateway;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;
import cn.momia.service.promo.coupon.Coupon;
import com.alibaba.fastjson.util.TypeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AlipayGateway extends AbstractPaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayGateway.class);

    @Override
    public Map<String, String> extractPrepayParams(HttpServletRequest request, Order order, Product product, Coupon coupon) {
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
        params.put(AlipayPrepayFields.TOTAL_FEE, String.valueOf(couponService.calcTotalFee(order.getTotalFee(), coupon).floatValue()));
        params.put(AlipayPrepayFields.BODY, product.getTitle());
        params.put(AlipayPrepayFields.IT_B_PAY, "30m");
        params.put(AlipayPrepayFields.SHOW_URL, "m.duolaqinzi.com");
        params.put(AlipayPrepayFields.SIGN, AlipayUtil.sign(params));

        return params;
    }

    @Override
    public PrepayResult doPrepay(PrepayParam param) {
        PrepayResult result = new PrepayResult();
        result.setSuccessful(param.get(AlipayPrepayFields.SIGN) != null);
        if (result.isSuccessful()) result.addAll(param.getAll());

        return result;
    }

    @Override
    protected boolean isPayedSuccessfully(CallbackParam param) {
        String tradeStatus = param.get(AlipayCallbackFields.TRADE_STATUS);
        if (!"TRADE_SUCCESS".equalsIgnoreCase(tradeStatus)) return false;

        String notifyId = param.get(AlipayCallbackFields.NOTIFY_ID);
        if (notifyId != null) return verifyResponse(notifyId);

        return false;
    }

    private boolean verifyResponse(String notifyId) {
        String partner = conf.getString("Payment.Ali.Partner");
        String verifyUrl = conf.getString("Payment.Ali.VerifyUrl") + "partner=" + partner + "&notify_id=" + notifyId;

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(verifyUrl);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("fail to execute request: " + request);
            }

            String entity = EntityUtils.toString(response.getEntity());

            return Boolean.valueOf(entity);
        } catch (Exception e) {
            return false;
        }
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
        payment.setPayer(param.get(AlipayCallbackFields.BUYER_ID));

        Date finishTime = TypeUtils.castToDate(param.get(AlipayCallbackFields.GMT_PAYMENT));
        payment.setFinishTime(finishTime == null ? new Date() : finishTime);

        payment.setPayType(Payment.Type.ALIPAY);
        payment.setTradeNo(param.get(AlipayCallbackFields.TRADE_NO));
        payment.setFee(new BigDecimal(param.get(AlipayCallbackFields.TOTAL_FEE)));

        return payment;
    }

    @Override
    protected CallbackResult buildFailCallbackResult() {
        CallbackResult result = new CallbackResult();
        result.setSuccessful(false);

        return result;
    }

    @Override
    protected CallbackResult buildSuccessCallbackResult() {
        CallbackResult result = new CallbackResult();
        result.setSuccessful(true);

        return result;
    }
}
