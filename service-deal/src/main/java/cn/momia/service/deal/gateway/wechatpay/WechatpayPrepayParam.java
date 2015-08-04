package cn.momia.service.deal.gateway.wechatpay;

import cn.momia.common.service.config.Configuration;
import cn.momia.common.service.exception.MomiaFailedException;
import cn.momia.service.deal.facade.OrderInfoFields;
import cn.momia.service.deal.gateway.PrepayParam;
import cn.momia.service.deal.gateway.TradeSourceType;
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

public class WechatpayPrepayParam extends PrepayParam {
    private static final String DATE_FORMAT_STR = "yyyyMMddHHmmss";
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT_STR);

    @Override
    public long getOrderId() {
        String outTradeNo = get(WechatpayPrepayFields.OUT_TRADE_NO);
        return Long.valueOf(outTradeNo.substring(0, outTradeNo.length() - DATE_FORMAT_STR.length()));
    }

    public WechatpayPrepayParam(Map<String, String> params) {
        String tradeType = params.get("trade_type");
        if ("APP".equals(tradeType)) {
            setTradeSourceType(TradeSourceType.APP);
            add(WechatpayPrepayFields.APPID, Configuration.getString("Payment.Wechat.AppAppId"));
            add(WechatpayPrepayFields.PRODUCT_ID, params.get(OrderInfoFields.PRODUCT_ID));
            add(WechatpayPrepayFields.MCH_ID, Configuration.getString("Payment.Wechat.AppMchId"));
        } else if ("JSAPI".equals(tradeType)) {
            setTradeSourceType(TradeSourceType.WAP);
            add(WechatpayPrepayFields.APPID, Configuration.getString("Payment.Wechat.JsApiAppId"));
            add(WechatpayPrepayFields.OPENID, getJsApiOpenId(params.get(WechatpayPrepayFields.CODE)));
            add(WechatpayPrepayFields.MCH_ID, Configuration.getString("Payment.Wechat.JsApiMchId"));
        } else {
            throw new MomiaFailedException("not supported trade type: " + tradeType);
        }

        add(WechatpayPrepayFields.NONCE_STR, WechatpayUtil.createNoncestr(32));
        add(WechatpayPrepayFields.BODY, params.get(OrderInfoFields.PRODUCT_TITLE));
        add(WechatpayPrepayFields.OUT_TRADE_NO, params.get(OrderInfoFields.ORDER_ID) + DATE_FORMATTER.format(new Date()));
        add(WechatpayPrepayFields.TOTAL_FEE, params.get(OrderInfoFields.TOTAL_FEE));
        add(WechatpayPrepayFields.SPBILL_CREATE_IP, params.get(OrderInfoFields.USER_IP));
        add(WechatpayPrepayFields.NOTIFY_URL, Configuration.getString("Payment.Wechat.NotifyUrl"));
        add(WechatpayPrepayFields.TRADE_TYPE, tradeType);
        add(WechatpayPrepayFields.TIME_EXPIRE, DATE_FORMATTER.format(new Date(System.currentTimeMillis() + 30 * 60 * 1000)));
        add(WechatpayPrepayFields.SIGN, WechatpayUtil.sign(all(), getTradeSourceType()));
    }

    private static String getJsApiOpenId(String code) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(Configuration.getString("Payment.Wechat.AccessTokenService"))
                    .append("?")
                    .append("appid=").append(Configuration.getString("Payment.Wechat.JsApiAppId"))
                    .append("&")
                    .append("secret=").append(Configuration.getSecretKey("wechatpayJsApiKey"))
                    .append("&")
                    .append("code=").append(code)
                    .append("&")
                    .append("grant_type=authorization_code");
            HttpGet request = new HttpGet(urlBuilder.toString());
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new MomiaFailedException("fail to execute request: " + request);
            }

            String entity = EntityUtils.toString(response.getEntity());
            JSONObject resultJson = JSON.parseObject(entity);

            if (resultJson.containsKey("openid")) return resultJson.getString("openid");

            throw new MomiaFailedException("fail to get openid");
        } catch (Exception e) {
            throw new MomiaFailedException("fail to get openid");
        }
    }
}
