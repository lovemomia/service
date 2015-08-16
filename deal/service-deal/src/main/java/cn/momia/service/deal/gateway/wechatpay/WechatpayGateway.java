package cn.momia.service.deal.gateway.wechatpay;

import cn.momia.api.base.exception.MomiaFailedException;
import cn.momia.service.base.util.XmlUtil;
import cn.momia.service.base.config.Configuration;
import cn.momia.service.deal.gateway.AbstractPaymentGateway;
import cn.momia.service.deal.gateway.PrepayParam;
import cn.momia.service.deal.gateway.PrepayResult;
import cn.momia.service.deal.gateway.ClientType;
import cn.momia.service.deal.payment.Payment;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class WechatpayGateway extends AbstractPaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatpayGateway.class);

    private static final String PREPAY_REQUEST_RETURN_CODE = "return_code";
    private static final String PREPAY_REQUEST_RETURN_MSG = "return_msg";
    private static final String PREPAY_REQUEST_RESULT_CODE = "result_code";
    private static final String PREPAY_REQUEST_PREPAY_ID = "prepay_id";

    private static final String SUCCESS = "SUCCESS";

    @Override
    public PrepayResult doPrepay(PrepayParam param) {
        PrepayResult result = ClientType.isFromApp(param.getClientType()) ? new WechatpayPrepayResult.App() : new WechatpayPrepayResult.JsApi();

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
        StringEntity entity = new StringEntity(XmlUtil.paramsToXml(param.getAll()), "UTF-8");
        entity.setContentType("application/xml");
        entity.setContentEncoding("UTF-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    private void processResponseEntity(PrepayResult result, String entity, int clientType) {
        Map<String, String> params = XmlUtil.xmlToParams(entity);
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
    protected int getPayType() {
        return Payment.Type.WECHATPAY;
    }
}
