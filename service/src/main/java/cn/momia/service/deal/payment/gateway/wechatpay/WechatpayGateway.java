package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.common.config.Configuration;
import cn.momia.common.misc.XmlUtil;
import cn.momia.common.web.misc.RequestUtil;
import cn.momia.common.web.secret.SecretKey;
import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.PaymentService;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;
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

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class WechatpayGateway implements PaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatpayGateway.class);

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final String SUCCESS = "SUCCESS";
    private static final String OK = "OK";
    private static final String FAIL = "FAIL";
    private static final String ERROR = "ERROR";

    private Configuration conf;
    private OrderService orderService;
    private PaymentService paymentService;

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Map<String, String> extractPrepayParams(HttpServletRequest request, Order order, Product product) {
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> httpParams = request.getParameterMap();
        String tradeType = httpParams.get(WechatpayPrepayFields.TRADE_TYPE)[0];
        if (tradeType.equals("NATIVE")) {
            params.put(WechatpayPrepayFields.APPID, conf.getString("Payment.Wechat.NativeAppId"));
            params.put(WechatpayPrepayFields.PRODUCT_ID, String.valueOf(product.getId()));
        } else if (tradeType.equals("JSAPI")) {
            params.put(WechatpayPrepayFields.APPID, conf.getString("Payment.Wechat.JsApiAppId"));
            params.put(WechatpayPrepayFields.OPENID, getJsApiOpenId(httpParams.get(WechatpayPrepayFields.CODE)[0]));
        } else {
            throw new RuntimeException("not supported trade type: " + tradeType);
        }

        params.put(WechatpayPrepayFields.MCH_ID, conf.getString("Payment.Wechat.MchId"));
        params.put(WechatpayPrepayFields.NONCE_STR, WechatpayUtil.createNoncestr(32));
        params.put(WechatpayPrepayFields.BODY, product.getTitle());
        params.put(WechatpayPrepayFields.OUT_TRADE_NO, String.valueOf(order.getId()));
        params.put(WechatpayPrepayFields.TOTAL_FEE, String.valueOf((int) (order.getTotalFee().floatValue() * 100)));
        params.put(WechatpayPrepayFields.SPBILL_CREATE_IP, RequestUtil.getRemoteIp(request));
        params.put(WechatpayPrepayFields.NOTIFY_URL, conf.getString("Payment.Wechat.NotifyUrl"));
        params.put(WechatpayPrepayFields.TRADE_TYPE, tradeType);
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
                    .append("secret=").append(SecretKey.get("wechatpayAppKey"))
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
    public PrepayResult prepay(PrepayParam param) {
        PrepayResult result = new PrepayResult();

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost request = createRequest(param);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("fail to execute request: " + request);
            }

            String entity = EntityUtils.toString(response.getEntity(), "utf-8");
            processResponseEntity(result, entity);
        } catch (Exception e) {
            LOGGER.error("fail to prepay", e);
            result.setSuccessful(false);
        }

        return result;
    }

    private HttpPost createRequest(PrepayParam param) {
        HttpPost httpPost = new HttpPost(conf.getString("Payment.Wechat.PrepayService"));
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/xml");
        StringEntity entity = new StringEntity(XmlUtil.paramsToXml(param.getAll()), "utf-8");
        entity.setContentType("application/xml");
        entity.setContentEncoding("utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    private void processResponseEntity(PrepayResult result, String entity) {
        Map<String, String> params = XmlUtil.xmlToParams(entity);

        String return_code = params.get(WechatpayPrepayFields.RETURN_CODE);
        String result_code = params.get(WechatpayPrepayFields.RESULT_CODE);

        boolean successful = return_code != null && return_code.equalsIgnoreCase(SUCCESS) && result_code != null && result_code.equalsIgnoreCase(SUCCESS);
        result.setSuccessful(successful);

        if (successful) {
            result.add(WechatpayPrepayFields.RETURN_CODE, params.get(WechatpayPrepayFields.RETURN_CODE));
            result.add(WechatpayPrepayFields.RESULT_CODE, params.get(WechatpayPrepayFields.RESULT_CODE));
            result.add(WechatpayPrepayFields.APPID, params.get(WechatpayPrepayFields.APPID));
            result.add(WechatpayPrepayFields.MCH_ID, params.get(WechatpayPrepayFields.MCH_ID));
            result.add(WechatpayPrepayFields.NONCE_STR, params.get(WechatpayPrepayFields.NONCE_STR));
            result.add(WechatpayPrepayFields.SIGN, params.get(WechatpayPrepayFields.SIGN));
            result.add(WechatpayPrepayFields.TRADE_TYPE, params.get(WechatpayPrepayFields.TRADE_TYPE));
            result.add(WechatpayPrepayFields.PREPAY_ID, params.get(WechatpayPrepayFields.PREPAY_ID));
        } else {
            LOGGER.error("fail to prepay: {}/{}", params.get(WechatpayPrepayFields.RETURN_CODE), params.get(WechatpayPrepayFields.RETURN_MSG));
        }
    }

    @Override
    public CallbackResult callback(CallbackParam param) {
        CallbackResult result = new CallbackResult();
        if (isPayedSuccessfully(param) && !finishPayment(param)) {
            result.add(WechatpayCallbackFields.RETURN_CODE, FAIL);
            result.add(WechatpayCallbackFields.RETURN_MSG, ERROR);
        } else {
            result.setSuccessful(true);
            result.add(WechatpayCallbackFields.RETURN_CODE, SUCCESS);
            result.add(WechatpayCallbackFields.RETURN_MSG, OK);
        }

        return result;
    }

    private boolean isPayedSuccessfully(CallbackParam param) {
        String return_code = param.get(WechatpayCallbackFields.RETURN_CODE);
        String result_code = param.get(WechatpayCallbackFields.RESULT_CODE);

        return return_code != null && return_code.equalsIgnoreCase(SUCCESS) &&
                result_code != null && result_code.equalsIgnoreCase(SUCCESS);
    }

    private boolean finishPayment(CallbackParam param) {
        try {
            if (!orderService.pay(Long.valueOf(param.get("out_trade_no")))) return false;
            logPayment(param);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void logPayment(CallbackParam param) {
        try {
            long paymentId = paymentService.add(createPayment(param));
            if (paymentId <= 0) LOGGER.error("fail to log payment: {}", param);
        } catch (Exception e) {
            LOGGER.error("fail to log payment: {}", param, e);
        }
    }

    private Payment createPayment(CallbackParam param) throws ParseException {
        Payment payment = new Payment();
        payment.setOrderId(Long.valueOf(param.get(WechatpayCallbackFields.OUT_TRADE_NO)));
        payment.setPayerId(param.get(WechatpayCallbackFields.OPEN_ID));
        payment.setFinishTime(DATE_FORMATTER.parse(param.get(WechatpayCallbackFields.TIME_END)));
        payment.setPayType(Payment.Type.WECHATPAY);
        payment.setTradeNo(param.get(WechatpayCallbackFields.TRANSACTION_ID));
        payment.setFee(new BigDecimal(param.get(WechatpayCallbackFields.TOTAL_FEE)).divide(new BigDecimal(100)));

        return payment;
    }
}
