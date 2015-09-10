package cn.momia.service.deal.gateway.wechatpay;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.util.XmlUtil;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.deal.gateway.CallbackParam;
import cn.momia.service.deal.gateway.CallbackResult;
import cn.momia.service.deal.gateway.PaymentGateway;
import cn.momia.service.deal.gateway.PrepayParam;
import cn.momia.service.deal.gateway.PrepayResult;
import cn.momia.service.deal.gateway.ClientType;
import cn.momia.service.deal.payment.Payment;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WechatpayGateway implements PaymentGateway {
    private static class PrepayRequestField {
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

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatpayGateway.class);

    private static final String DATE_FORMAT_STR = "yyyyMMddHHmmss";
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT_STR);

    private static final String PREPAY_REQUEST_RETURN_CODE = "return_code";
    private static final String PREPAY_REQUEST_RETURN_MSG = "return_msg";
    private static final String PREPAY_REQUEST_RESULT_CODE = "result_code";
    private static final String PREPAY_REQUEST_PREPAY_ID = "prepay_id";

    private static final String SUCCESS = "SUCCESS";

    @Override
    public PrepayResult prepay(PrepayParam param) {
        PrepayResult result = WechatpayPrepayResult.create(param.getClientType());

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost request = createRequest(param);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new MomiaFailedException("fail to execute request: " + request);
            }

            String entity = EntityUtils.toString(response.getEntity(), "UTF-8");
            processResponseEntity(result, entity, param.getClientType());
        } catch (Exception e) {
            LOGGER.error("fail to prepay", e);
            result.setSuccessful(false);
        }

        return result;
    }

    private HttpPost createRequest(PrepayParam param) {
        HttpPost httpPost = new HttpPost(Configuration.getString("Payment.Wechat.PrepayService"));
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/xml");
        StringEntity entity = new StringEntity(XmlUtil.mapToXml(createRequestParams(param)), "UTF-8");
        entity.setContentType("application/xml");
        entity.setContentEncoding("UTF-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    private Map<String, String> createRequestParams(PrepayParam param) {
        Map<String, String> requestParams = new HashMap<String, String>();

        int clientType = param.getClientType();
        switch (clientType) {
            case ClientType.APP:
                requestParams.put(PrepayRequestField.APPID, Configuration.getString("Payment.Wechat.AppAppId"));
                requestParams.put(PrepayRequestField.PRODUCT_ID, String.valueOf(param.getProductId()));
                requestParams.put(PrepayRequestField.MCH_ID, Configuration.getString("Payment.Wechat.AppMchId"));
                break;
            case ClientType.WAP:
                requestParams.put(PrepayRequestField.APPID, Configuration.getString("Payment.Wechat.JsApiAppId"));
                requestParams.put(PrepayRequestField.OPENID, getJsApiOpenId(param.get(PrepayRequestField.CODE)));
                requestParams.put(PrepayRequestField.MCH_ID, Configuration.getString("Payment.Wechat.JsApiMchId"));
                break;
            default: new MomiaFailedException("not supported client type: " + clientType);
        }

        requestParams.put(PrepayRequestField.NONCE_STR, WechatpayUtil.createNoncestr(32));
        requestParams.put(PrepayRequestField.BODY, param.getProductTitle());
        requestParams.put(PrepayRequestField.OUT_TRADE_NO, param.getOrderId() + DATE_FORMATTER.format(new Date()));
        requestParams.put(PrepayRequestField.TOTAL_FEE, String.valueOf(param.getTotalFee()));
        requestParams.put(PrepayRequestField.SPBILL_CREATE_IP, param.get("userIp"));
        requestParams.put(PrepayRequestField.NOTIFY_URL, Configuration.getString("Payment.Wechat.NotifyUrl"));
        requestParams.put(PrepayRequestField.TRADE_TYPE, param.get(PrepayRequestField.TRADE_TYPE));
        requestParams.put(PrepayRequestField.TIME_EXPIRE, DATE_FORMATTER.format(new Date(System.currentTimeMillis() + 30 * 60 * 1000)));
        requestParams.put(PrepayRequestField.SIGN, WechatpayUtil.sign(requestParams, clientType));

        return requestParams;
    }

    private static String getJsApiOpenId(String code) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(Configuration.getString("Payment.Wechat.AccessTokenService"))
                    .append("?")
                    .append("appid=").append(Configuration.getString("Payment.Wechat.JsApiAppId"))
                    .append("&")
                    .append("secret=").append(Configuration.getString("Payment.Wechat.JsApiSecret"))
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

    private void processResponseEntity(PrepayResult result, String entity, int clientType) {
        Map<String, String> params = XmlUtil.xmlToMap(entity);
        String return_code = params.get(PREPAY_REQUEST_RETURN_CODE);
        String result_code = params.get(PREPAY_REQUEST_RESULT_CODE);

        boolean successful = return_code != null && return_code.equalsIgnoreCase(SUCCESS) && result_code != null && result_code.equalsIgnoreCase(SUCCESS);
        result.setSuccessful(successful);

        if (successful) {
            if (!WechatpayUtil.validateSign(params, clientType)) throw new MomiaFailedException("fail to prepay, invalid sign");

            if (ClientType.isFromApp(clientType)) {
                result.add(WechatpayPrepayResult.App.Field.APPID, Configuration.getString("Payment.Wechat.AppAppId"));
                result.add(WechatpayPrepayResult.App.Field.PARTNERID, Configuration.getString("Payment.Wechat.AppMchId"));
                result.add(WechatpayPrepayResult.App.Field.PREPAYID, params.get(PREPAY_REQUEST_PREPAY_ID));
                result.add(WechatpayPrepayResult.App.Field.PACKAGE, "Sign=WXPay");
                result.add(WechatpayPrepayResult.App.Field.NONCE_STR, WechatpayUtil.createNoncestr(32));
                result.add(WechatpayPrepayResult.App.Field.TIMESTAMP, String.valueOf(new Date().getTime()).substring(0, 10));
                result.add(WechatpayPrepayResult.App.Field.SIGN, WechatpayUtil.sign(result.getAll(), clientType));
            } else if (ClientType.isFromWap(clientType)) {
                result.add(WechatpayPrepayResult.JsApi.Field.APPID, Configuration.getString("Payment.Wechat.JsApiAppId"));
                result.add(WechatpayPrepayResult.JsApi.Field.PACKAGE, "prepay_id=" + params.get(PREPAY_REQUEST_PREPAY_ID));
                result.add(WechatpayPrepayResult.JsApi.Field.NONCE_STR, WechatpayUtil.createNoncestr(32));
                result.add(WechatpayPrepayResult.JsApi.Field.TIMESTAMP, String.valueOf(new Date().getTime()).substring(0, 10));
                result.add(WechatpayPrepayResult.JsApi.Field.SIGN_TYPE, "MD5");
                result.add(WechatpayPrepayResult.JsApi.Field.PAY_SIGN, WechatpayUtil.sign(result.getAll(), clientType));
            } else {
                throw new MomiaFailedException("unsupported trade source type: " + clientType);
            }
        } else {
            LOGGER.error("fail to prepay: {}/{}/{}", params.get(PREPAY_REQUEST_RETURN_CODE), params.get(PREPAY_REQUEST_RESULT_CODE), params.get(PREPAY_REQUEST_RETURN_MSG));
        }
    }

    @Override
    public CallbackResult callback(CallbackParam param) {
        CallbackResult result = new CallbackResult();
        result.setOrderId(param.getOrderId());
        result.setPayType(Payment.Type.WECHATPAY);

        if (param.isPayedSuccessfully()) {
            result.setSuccessful(true);
            result.setPayer(param.getPayer());
            result.setFinishTime(param.getFinishTime());
            result.setTradeNo(param.getTradeNo());
            result.setTotalFee(param.getTotalFee());
        }

        return result;
    }
}
