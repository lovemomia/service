package cn.momia.service.deal.gateway.wechatpay;

import cn.momia.service.base.config.Configuration;
import cn.momia.api.base.exception.MomiaFailedException;
import cn.momia.service.deal.facade.OrderInfoFields;
import cn.momia.service.deal.gateway.PrepayParam;
import cn.momia.service.deal.gateway.ClientType;
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
    private static class Field {
        public static final String APPID = "appid"; //微信公众号id
        public static final String MCH_ID = "mch_id"; //商户id
        public static final String NONCE_STR = "nonce_str"; //随机字符串
        public static final String SIGN = "sign"; //签名
        public static final String BODY = "body"; //商品描述
        public static final String OUT_TRADE_NO = "out_trade_no"; //商户订单号
        public static final String TOTAL_FEE = "total_fee"; //总金额
        public static final String SPBILL_CREATE_IP = "spbill_create_ip"; //终端IP
        public static final String NOTIFY_URL = "notify_url"; //通知地址
        public static final String PRODUCT_ID = "product_id"; //通知地址
        public static final String OPENID = "openid"; //通知地址
        public static final String TRADE_TYPE = "trade_type";
        public static final String TIME_EXPIRE = "time_expire";
        public static final String CODE = "code";
    }

    private static final String DATE_FORMAT_STR = "yyyyMMddHHmmss";
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT_STR);

    @Override
    public long getOrderId() {
        String outTradeNo = get(Field.OUT_TRADE_NO);
        return Long.valueOf(outTradeNo.substring(0, outTradeNo.length() - DATE_FORMAT_STR.length()));
    }

    public WechatpayPrepayParam(Map<String, String> params) {
        String tradeType = params.get("trade_type");
        if ("APP".equals(tradeType)) {
            setClientType(ClientType.APP);
            add(Field.APPID, Configuration.getString("Payment.Wechat.AppAppId"));
            add(Field.PRODUCT_ID, params.get(OrderInfoFields.PRODUCT_ID));
            add(Field.MCH_ID, Configuration.getString("Payment.Wechat.AppMchId"));
        } else if ("JSAPI".equals(tradeType)) {
            setClientType(ClientType.WAP);
            add(Field.APPID, Configuration.getString("Payment.Wechat.JsApiAppId"));
            add(Field.OPENID, getJsApiOpenId(params.get(Field.CODE)));
            add(Field.MCH_ID, Configuration.getString("Payment.Wechat.JsApiMchId"));
        } else {
            throw new MomiaFailedException("not supported trade type: " + tradeType);
        }

        add(Field.NONCE_STR, WechatpayUtil.createNoncestr(32));
        add(Field.BODY, params.get(OrderInfoFields.PRODUCT_TITLE));
        add(Field.OUT_TRADE_NO, params.get(OrderInfoFields.ORDER_ID) + DATE_FORMATTER.format(new Date()));
        add(Field.TOTAL_FEE, params.get(OrderInfoFields.TOTAL_FEE));
        add(Field.SPBILL_CREATE_IP, params.get(OrderInfoFields.USER_IP));
        add(Field.NOTIFY_URL, Configuration.getString("Payment.Wechat.NotifyUrl"));
        add(Field.TRADE_TYPE, tradeType);
        add(Field.TIME_EXPIRE, DATE_FORMATTER.format(new Date(System.currentTimeMillis() + 30 * 60 * 1000)));
        add(Field.SIGN, WechatpayUtil.sign(getAll(), getClientType()));
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
