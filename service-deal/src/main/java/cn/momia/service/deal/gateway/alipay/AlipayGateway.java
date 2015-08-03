package cn.momia.service.deal.gateway.alipay;

import cn.momia.common.service.config.Configuration;
import cn.momia.service.deal.gateway.AbstractPaymentGateway;
import cn.momia.service.deal.gateway.CallbackParam;
import cn.momia.service.deal.gateway.PrepayResult;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.gateway.PrepayParam;
import com.alibaba.fastjson.util.TypeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

public class AlipayGateway extends AbstractPaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayGateway.class);

    @Override
    protected long getPrepayOutTradeNo(PrepayParam param) {
        return Long.valueOf(param.get(AlipayPrepayFields.OUT_TRADE_NO));
    }

    @Override
    public PrepayResult doPrepay(PrepayParam param) {
        PrepayResult result = new AlipayPrepayResult();

        String type = param.get(AlipayPrepayFields.TYPE);

        if ("app".equalsIgnoreCase(type)) result.add(AlipayPrepayFields.SERVICE, Configuration.getString("Payment.Ali.AppService"));
        else result.add(AlipayPrepayFields.SERVICE, Configuration.getString("Payment.Ali.WapService"));
        result.add(AlipayPrepayFields.PARTNER, Configuration.getString("Payment.Ali.Partner"));
        result.add(AlipayPrepayFields.INPUT_CHARSET, "utf-8");
        result.add(AlipayPrepayFields.SIGN_TYPE, "RSA");
        result.add(AlipayPrepayFields.NOTIFY_URL, Configuration.getString("Payment.Ali.NotifyUrl"));
        result.add(AlipayPrepayFields.OUT_TRADE_NO, param.get(AlipayPrepayFields.OUT_TRADE_NO));
        result.add(AlipayPrepayFields.SUBJECT, param.get(AlipayPrepayFields.SUBJECT));
        result.add(AlipayPrepayFields.PAYMENT_TYPE, "1");
        result.add(AlipayPrepayFields.SELLER_ID, Configuration.getString("Payment.Ali.Partner"));
        result.add(AlipayPrepayFields.TOTAL_FEE, param.get(AlipayPrepayFields.TOTAL_FEE));
        result.add(AlipayPrepayFields.BODY, param.get(AlipayPrepayFields.BODY));
        result.add(AlipayPrepayFields.IT_B_PAY, "30m");
        result.add(AlipayPrepayFields.SHOW_URL, Configuration.getString("Wap.Domain"));
        if ("wap".equalsIgnoreCase(type)) result.add(AlipayPrepayFields.RETURN_URL, param.get(AlipayPrepayFields.RETURN_URL));
        result.add(AlipayPrepayFields.SIGN, AlipayUtil.sign(result.all(), type));

        result.setSuccessful(!StringUtils.isBlank(result.get(AlipayPrepayFields.SIGN)));

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
        String partner = Configuration.getString("Payment.Ali.Partner");
        String verifyUrl = Configuration.getString("Payment.Ali.VerifyUrl") + "partner=" + partner + "&notify_id=" + notifyId;

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
        boolean successful = AlipayUtil.validateSign(param.all());
        if (!successful) LOGGER.warn("invalid sign, order id: {} ", param.get(AlipayCallbackFields.OUT_TRADE_NO));

        return successful;
    }

    @Override
    protected long getCallbackOutTradeNo(CallbackParam param) {
        return Long.valueOf(param.get(AlipayCallbackFields.OUT_TRADE_NO));
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
}
