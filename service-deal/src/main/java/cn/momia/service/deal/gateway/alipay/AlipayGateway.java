package cn.momia.service.deal.gateway.alipay;

import cn.momia.common.service.config.Configuration;
import cn.momia.service.deal.gateway.AbstractPaymentGateway;
import cn.momia.service.deal.gateway.CallbackParam;
import cn.momia.service.deal.gateway.PrepayResult;
import cn.momia.service.deal.gateway.TradeSourceType;
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
        return Long.valueOf(param.get(AlipayPrepayResult.Field.OUT_TRADE_NO));
    }

    @Override
    public PrepayResult doPrepay(PrepayParam param) {
        PrepayResult result = new AlipayPrepayResult();

        if (TradeSourceType.isFromApp(param.getTradeSourceType())) {
            result.add(AlipayPrepayResult.Field.SERVICE, Configuration.getString("Payment.Ali.AppService"));
        } else if (TradeSourceType.isFromWap(param.getTradeSourceType())) {
            result.add(AlipayPrepayResult.Field.SERVICE, Configuration.getString("Payment.Ali.WapService"));
            result.add(AlipayPrepayResult.Field.RETURN_URL, param.get(AlipayPrepayResult.Field.RETURN_URL));
        }

        result.add(AlipayPrepayResult.Field.PARTNER, Configuration.getString("Payment.Ali.Partner"));
        result.add(AlipayPrepayResult.Field.INPUT_CHARSET, "utf-8");
        result.add(AlipayPrepayResult.Field.SIGN_TYPE, "RSA");
        result.add(AlipayPrepayResult.Field.NOTIFY_URL, Configuration.getString("Payment.Ali.NotifyUrl"));
        result.add(AlipayPrepayResult.Field.OUT_TRADE_NO, param.get(AlipayPrepayResult.Field.OUT_TRADE_NO));
        result.add(AlipayPrepayResult.Field.SUBJECT, param.get(AlipayPrepayResult.Field.SUBJECT));
        result.add(AlipayPrepayResult.Field.PAYMENT_TYPE, "1");
        result.add(AlipayPrepayResult.Field.SELLER_ID, Configuration.getString("Payment.Ali.Partner"));
        result.add(AlipayPrepayResult.Field.TOTAL_FEE, param.get(AlipayPrepayResult.Field.TOTAL_FEE));
        result.add(AlipayPrepayResult.Field.BODY, param.get(AlipayPrepayResult.Field.BODY));
        result.add(AlipayPrepayResult.Field.IT_B_PAY, "30m");
        result.add(AlipayPrepayResult.Field.SHOW_URL, Configuration.getString("Wap.Domain"));
        result.add(AlipayPrepayResult.Field.SIGN, AlipayUtil.sign(result.all(), param.getTradeSourceType()));

        result.setSuccessful(!StringUtils.isBlank(result.get(AlipayPrepayResult.Field.SIGN)));

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
