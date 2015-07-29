package cn.momia.service.deal.gateway.factory;

import cn.momia.common.config.Configuration;
import cn.momia.common.service.exception.MomiaFailedException;
import cn.momia.common.service.secret.SecretKey;
import cn.momia.service.deal.OrderInfoFields;
import cn.momia.service.deal.gateway.PrepayParam;
import cn.momia.service.deal.gateway.alipay.AlipayPrepayFields;
import cn.momia.service.deal.gateway.alipay.AlipayUtil;
import cn.momia.service.deal.gateway.wechatpay.WechatpayPrepayFields;
import cn.momia.service.deal.gateway.wechatpay.WechatpayUtil;
import cn.momia.service.deal.payment.Payment;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PrepayParamFactory {
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");

    private static Configuration conf;

    public void setConf(Configuration conf) {
        PrepayParamFactory.conf = conf;
    }

    public static PrepayParam create(Map<String, String> params, int payType) {
        PrepayParam prepayParam = createPrepayParam(payType);
        switch (payType) {
            case Payment.Type.ALIPAY:
                prepayParam.add(AlipayPrepayFields.SERVICE, "mobile.securitypay.pay");
                prepayParam.add(AlipayPrepayFields.PARTNER, conf.getString("Payment.Ali.Partner"));
                prepayParam.add(AlipayPrepayFields.INPUT_CHARSET, "utf-8");
                prepayParam.add(AlipayPrepayFields.SIGN_TYPE, "RSA");
                prepayParam.add(AlipayPrepayFields.NOTIFY_URL, conf.getString("Payment.Ali.NotifyUrl"));
                prepayParam.add(AlipayPrepayFields.OUT_TRADE_NO, params.get(OrderInfoFields.ORDER_ID));
                prepayParam.add(AlipayPrepayFields.SUBJECT, params.get(OrderInfoFields.PRODUCT_TITLE));
                prepayParam.add(AlipayPrepayFields.PAYMENT_TYPE, "1");
                prepayParam.add(AlipayPrepayFields.SELLER_ID, conf.getString("Payment.Ali.Partner"));
                prepayParam.add(AlipayPrepayFields.TOTAL_FEE, params.get(OrderInfoFields.TOTAL_FEE));
                prepayParam.add(AlipayPrepayFields.BODY, params.get(OrderInfoFields.PRODUCT_TITLE));
                prepayParam.add(AlipayPrepayFields.IT_B_PAY, "30m");
                prepayParam.add(AlipayPrepayFields.SHOW_URL, "m.duolaqinzi.com");
                prepayParam.add(AlipayPrepayFields.SIGN, AlipayUtil.sign(prepayParam.getAll()));

                break;
            case Payment.Type.WECHATPAY:
                String tradeType = params.get(WechatpayPrepayFields.TRADE_TYPE);
                if (tradeType.equals("APP")) {
                    prepayParam.add(WechatpayPrepayFields.APPID, conf.getString("Payment.Wechat.AppAppId"));
                    prepayParam.add(WechatpayPrepayFields.PRODUCT_ID, params.get(OrderInfoFields.PRODUCT_ID));
                    prepayParam.add(WechatpayPrepayFields.MCH_ID, conf.getString("Payment.Wechat.AppMchId"));
                } else if (tradeType.equals("JSAPI")) {
                    prepayParam.add(WechatpayPrepayFields.APPID, conf.getString("Payment.Wechat.JsApiAppId"));
                    prepayParam.add(WechatpayPrepayFields.OPENID, getJsApiOpenId(params.get(WechatpayPrepayFields.CODE)));
                    prepayParam.add(WechatpayPrepayFields.MCH_ID, conf.getString("Payment.Wechat.JsApiMchId"));
                } else {
                    throw new RuntimeException("not supported trade type: " + tradeType);
                }

                prepayParam.add(WechatpayPrepayFields.NONCE_STR, WechatpayUtil.createNoncestr(32));
                prepayParam.add(WechatpayPrepayFields.BODY, params.get(OrderInfoFields.PRODUCT_TITLE));
                prepayParam.add(WechatpayPrepayFields.OUT_TRADE_NO, params.get(OrderInfoFields.ORDER_ID) + DATE_FORMATTER.format(new Date()));
                prepayParam.add(WechatpayPrepayFields.TOTAL_FEE, params.get(OrderInfoFields.TOTAL_FEE));
                prepayParam.add(WechatpayPrepayFields.SPBILL_CREATE_IP, params.get(OrderInfoFields.USER_IP));
                prepayParam.add(WechatpayPrepayFields.NOTIFY_URL, conf.getString("Payment.Wechat.NotifyUrl"));
                prepayParam.add(WechatpayPrepayFields.TRADE_TYPE, tradeType);
                prepayParam.add(WechatpayPrepayFields.TIME_EXPIRE, DATE_FORMATTER.format(new Date(System.currentTimeMillis() + 30 * 60 * 1000)));
                prepayParam.add(WechatpayPrepayFields.SIGN, WechatpayUtil.sign(params, tradeType));

                break;
            default: throw new MomiaFailedException("无效的支付类型: " + payType);
        }

        return prepayParam;
    }

    private static PrepayParam createPrepayParam(int payType) {
        return new PrepayParam();
    }

    private static String getJsApiOpenId(String code) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(conf.getString("Payment.Wechat.AccessTokenService"))
                    .append("?")
                    .append("appid=").append(conf.getString("Payment.Wechat.JsApiAppId"))
                    .append("&")
                    .append("secret=").append(SecretKey.get("wechatpayJsApiKey"))
                    .append("&")
                    .append("code=").append(code)
                    .append("&")
                    .append("grant_type=authorization_code");
            HttpGet request = new HttpGet(urlBuilder.toString());
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("fail to execute request: " + request);
            }

            String entity = EntityUtils.toString(response.getEntity());
            JSONObject resultJson = JSON.parseObject(entity);

            if (resultJson.containsKey("openid")) return resultJson.getString("openid");

            throw new RuntimeException("fail to get openid");
        } catch (Exception e) {
            throw new RuntimeException("fail to get openid");
        }
    }
}
