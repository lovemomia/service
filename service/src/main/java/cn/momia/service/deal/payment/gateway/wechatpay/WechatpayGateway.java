package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.common.misc.XmlUtil;
import cn.momia.common.web.misc.RequestUtil;
import cn.momia.common.web.secret.SecretKey;
import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.AbstractPaymentGateway;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;
import cn.momia.service.promo.coupon.Coupon;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WechatpayGateway extends AbstractPaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatpayGateway.class);

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final String SUCCESS = "SUCCESS";
    private static final String OK = "OK";
    private static final String FAIL = "FAIL";
    private static final String ERROR = "ERROR";

    @Override
    public Map<String, String> extractPrepayParams(HttpServletRequest request, Order order, Product product, Coupon coupon) {
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> httpParams = request.getParameterMap();
        String tradeType = httpParams.get(WechatpayPrepayFields.TRADE_TYPE)[0];
        if (tradeType.equals("APP")) {
            params.put(WechatpayPrepayFields.APPID, conf.getString("Payment.Wechat.AppAppId"));
            params.put(WechatpayPrepayFields.PRODUCT_ID, String.valueOf(product.getId()));
            params.put(WechatpayPrepayFields.MCH_ID, conf.getString("Payment.Wechat.AppMchId"));
        } else if (tradeType.equals("JSAPI")) {
            params.put(WechatpayPrepayFields.APPID, conf.getString("Payment.Wechat.JsApiAppId"));
            params.put(WechatpayPrepayFields.OPENID, getJsApiOpenId(httpParams.get(WechatpayPrepayFields.CODE)[0]));
            params.put(WechatpayPrepayFields.MCH_ID, conf.getString("Payment.Wechat.JsApiMchId"));
        } else {
            throw new RuntimeException("not supported trade type: " + tradeType);
        }

        params.put(WechatpayPrepayFields.NONCE_STR, WechatpayUtil.createNoncestr(32));
        params.put(WechatpayPrepayFields.BODY, product.getTitle());
        params.put(WechatpayPrepayFields.OUT_TRADE_NO, String.valueOf(order.getId()));
        params.put(WechatpayPrepayFields.TOTAL_FEE, String.valueOf((int) (couponService.calcTotalFee(order.getTotalFee(), coupon).floatValue() * 100)));
        params.put(WechatpayPrepayFields.SPBILL_CREATE_IP, RequestUtil.getRemoteIp(request));
        params.put(WechatpayPrepayFields.NOTIFY_URL, conf.getString("Payment.Wechat.NotifyUrl"));
        params.put(WechatpayPrepayFields.TRADE_TYPE, tradeType);
        params.put(WechatpayPrepayFields.TIME_EXPIRE, DATE_FORMATTER.format(new Date(System.currentTimeMillis() + 30 * 60 * 1000)));
        params.put(WechatpayPrepayFields.SIGN, WechatpayUtil.sign(params, tradeType));

        return params;
    }

    private String getJsApiOpenId(String code) {
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

    @Override
    public PrepayResult doPrepay(PrepayParam param) {
        PrepayResult result = new PrepayResult();

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost request = createRequest(param);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("fail to execute request: " + request);
            }

            String entity = EntityUtils.toString(response.getEntity(), "UTF-8");
            processResponseEntity(result, entity, param.get(WechatpayPrepayFields.TRADE_TYPE));
        } catch (Exception e) {
            LOGGER.error("fail to prepay", e);
            result.setSuccessful(false);
        }

        return result;
    }

    private HttpPost createRequest(PrepayParam param) {
        HttpPost httpPost = new HttpPost(conf.getString("Payment.Wechat.PrepayService"));
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/xml");
        StringEntity entity = new StringEntity(XmlUtil.paramsToXml(param.getAll()), "UTF-8");
        entity.setContentType("application/xml");
        entity.setContentEncoding("UTF-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    private void processResponseEntity(PrepayResult result, String entity, String tradeType) {
        Map<String, String> params = XmlUtil.xmlToParams(entity);
        String return_code = params.get(WechatpayPrepayFields.RETURN_CODE);
        String result_code = params.get(WechatpayPrepayFields.RESULT_CODE);

        boolean successful = return_code != null && return_code.equalsIgnoreCase(SUCCESS) && result_code != null && result_code.equalsIgnoreCase(SUCCESS);
        result.setSuccessful(successful);

        if (successful) {
            if (!WechatpayUtil.validateSign(params, tradeType)) throw new RuntimeException("fail to prepay, invalid sign");

            if (tradeType.equals("APP")) {
                result.add(WechatpayPrepayFields.PREPAY_RESULT_APP_APPID, conf.getString("Payment.Wechat.AppAppId"));
                result.add(WechatpayPrepayFields.PREPAY_RESULT_APP_PARTNERID, conf.getString("Payment.Wechat.AppMchId"));
                result.add(WechatpayPrepayFields.PREPAY_RESULT_APP_PREPAYID, params.get(WechatpayPrepayFields.PREPAY_ID));
                result.add(WechatpayPrepayFields.PREPAY_RESULT_APP_PACKAGE, "Sign=WXPay");
                result.add(WechatpayPrepayFields.PREPAY_RESULT_APP_NONCE_STR, WechatpayUtil.createNoncestr(32));
                result.add(WechatpayPrepayFields.PREPAY_RESULT_APP_TIMESTAMP, String.valueOf(new Date().getTime()).substring(0, 10));
                result.add(WechatpayPrepayFields.PREPAY_RESULT_APP_SIGN, WechatpayUtil.sign(result.getAll(), tradeType));
            } else if (tradeType.equals("JSAPI")) {
                result.add(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_APPID, conf.getString("Payment.Wechat.JsApiAppId"));
                result.add(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_PACKAGE, "prepay_id=" + params.get(WechatpayPrepayFields.PREPAY_ID));
                result.add(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_NONCE_STR, WechatpayUtil.createNoncestr(32));
                result.add(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_TIMESTAMP, String.valueOf(new Date().getTime()).substring(0, 10));
                result.add(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_SIGN_TYPE, "MD5");
                result.add(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_PAY_SIGN, WechatpayUtil.sign(result.getAll(), tradeType));
            } else {
                throw new RuntimeException("unsupported trade type: " + tradeType);
            }
        } else {
            LOGGER.error("fail to prepay: {}/{}", params.get(WechatpayPrepayFields.RETURN_CODE), params.get(WechatpayPrepayFields.RETURN_MSG));
        }
    }

    @Override
    protected boolean isPayedSuccessfully(CallbackParam param) {
        String return_code = param.get(WechatpayCallbackFields.RETURN_CODE);
        String result_code = param.get(WechatpayCallbackFields.RESULT_CODE);

        return return_code != null && return_code.equalsIgnoreCase(SUCCESS) &&
                result_code != null && result_code.equalsIgnoreCase(SUCCESS);
    }

    @Override
    protected boolean validateCallbackSign(CallbackParam param) {
        String tradeType = param.get(WechatpayPrepayFields.TRADE_TYPE);
        boolean successful = WechatpayUtil.validateSign(param.getAll(), tradeType);
        if (!successful) LOGGER.warn("invalid sign, order id: {} ", param.get(WechatpayCallbackFields.OUT_TRADE_NO));

        return successful;
    }

    @Override
    protected Payment createPayment(CallbackParam param) {
        Payment payment = new Payment();
        payment.setOrderId(Long.valueOf(param.get(WechatpayCallbackFields.OUT_TRADE_NO)));
        payment.setPayer(param.get(WechatpayCallbackFields.OPEN_ID));

        Date finishTime;
        try {
            finishTime = DATE_FORMATTER.parse(param.get(WechatpayCallbackFields.TIME_END));
        } catch (ParseException e) {
            finishTime = new Date();
        }
        payment.setFinishTime(finishTime == null ? new Date() : finishTime);

        payment.setPayType(Payment.Type.WECHATPAY);
        payment.setTradeNo(param.get(WechatpayCallbackFields.TRANSACTION_ID));
        payment.setFee(new BigDecimal(param.get(WechatpayCallbackFields.TOTAL_FEE)).divide(new BigDecimal(100)));

        return payment;
    }

    @Override
    protected CallbackResult buildFailCallbackResult() {
        CallbackResult result = new CallbackResult();
        result.setSuccessful(false);
        result.add(WechatpayCallbackFields.RETURN_CODE, FAIL);
        result.add(WechatpayCallbackFields.RETURN_MSG, ERROR);

        return result;
    }

    @Override
    protected CallbackResult buildSuccessCallbackResult() {
        CallbackResult result = new CallbackResult();
        result.setSuccessful(true);
        result.add(WechatpayCallbackFields.RETURN_CODE, SUCCESS);
        result.add(WechatpayCallbackFields.RETURN_MSG, OK);

        return result;
    }
}
