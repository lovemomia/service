package cn.momia.service.deal.payment.gateway.factory;

import cn.momia.common.config.Configuration;
import cn.momia.common.web.secret.SecretKey;
import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.wechatpay.WechatpayPrepayFields;
import cn.momia.service.deal.payment.gateway.wechatpay.WechatpayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Map;

public class PrepayParamFactory {
    private static Configuration conf;

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public static PrepayParam create(Map<String, String[]> httpParams, Product product, Order order, int payType) {
        switch (payType) {
            case Payment.Type.ALIPAY:
                // TODO
                return null;
            case Payment.Type.WECHATPAY:
                return createWechatpayPrepayParam(httpParams, product, order);
            default:
                throw new RuntimeException("invalid pay type: " + payType);
        }
    }

    private static PrepayParam createWechatpayPrepayParam(Map<String, String[]> httpParams, Product product, Order order) {
        PrepayParam prepayParam = new PrepayParam();
        String tradeType = httpParams.get(WechatpayPrepayFields.TRADE_TYPE)[0];
        if (tradeType.equals("NATIVE")) {
            prepayParam.add(WechatpayPrepayFields.APPID, conf.getString("Payment.Wechat.NativeAppId"));
            prepayParam.add(WechatpayPrepayFields.PRODUCT_ID, String.valueOf(product.getId()));
        } else if (tradeType.equals("JSAPI")) {
            prepayParam.add(WechatpayPrepayFields.APPID, conf.getString("Payment.Wechat.JsApiAppId"));
            prepayParam.add(WechatpayPrepayFields.OPENID, getJsApiOpenId(httpParams.get(WechatpayPrepayFields.CODE)[0]));
        } else {
            throw new RuntimeException("not supported trade type: " + tradeType);
        }

        prepayParam.add(WechatpayPrepayFields.MCH_ID, conf.getString("Payment.Wechat.MchId"));
        prepayParam.add(WechatpayPrepayFields.NONCE_STR, WechatpayUtil.createNoncestr(32));
        prepayParam.add(WechatpayPrepayFields.BODY, product.getTitle());
        prepayParam.add(WechatpayPrepayFields.OUT_TRADE_NO, String.valueOf(order.getId()));
        prepayParam.add(WechatpayPrepayFields.TOTAL_FEE, String.valueOf(order.getTotalFee().floatValue() * 100));
        prepayParam.add(WechatpayPrepayFields.SPBILL_CREATE_IP, httpParams.get(WechatpayPrepayFields.SPBILL_CREATE_IP)[0]);
        prepayParam.add(WechatpayPrepayFields.NOTIFY_URL, conf.getString("Payment.Wechat.NotifyUrl"));
        prepayParam.add(WechatpayPrepayFields.TRADE_TYPE, tradeType);
        prepayParam.add(WechatpayPrepayFields.SIGN, WechatpayUtil.sign(prepayParam, tradeType));

        return prepayParam;
    }

    private static String getJsApiOpenId(String code) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(conf.getString("Payment.Wechat.AccessTokenService"))
                    .append("?")
                    .append("appid=").append(conf.getString("Payment.Wechat.JsApiAppId"))
                    .append("&")
                    .append("secret=").append(SecretKey.get("wechatpayJsApi"))
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

            return resultJson.getString("openid");
        } catch (Exception e) {
            throw new RuntimeException("fail to get openid");
        }
    }
}
