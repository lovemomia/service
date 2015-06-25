package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.common.encrypt.CommonUtil;
import cn.momia.common.encrypt.XmlUtil;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.AbstractPaymentGateway;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WechatpayGateway extends AbstractPaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatpayGateway.class);

    @Override
    public PrepayResult prepay(PrepayParam param) {
        String url = conf.getString("Payment.Wechat.PrepayService");

        return httpClientPostXml(url, (WechatpayPrepayParam) param);
    }

    public WechatpayPrepayResult httpClientPostXml(String url, WechatpayPrepayParam payParam) {
        BufferedReader in = null;
        HttpClient client = null;
        WechatpayPrepayResult prepayResult = null;
        Integer statusCode = -1;
        if (!url.equals("")) {
            if (client == null) {
                client = new DefaultHttpClient();
            }

            HttpPost post = new HttpPost(url);
            HashMap<String, String> map = new HashMap<String, String>();
            map = payParam.getHashMapParam(payParam, 2);
            String xmlStr = CommonUtil.ArrayToXml(map);
            StringEntity entity = null;
            try {
                entity = new StringEntity(xmlStr);
                post.setEntity(entity);
                post.setHeader("Content-Type", "text/xml;charset=UTF-8");
                HttpResponse response = client.execute(post);
                statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    String entityStr = EntityUtils.toString(response.getEntity());
                    Map m = XmlUtil.doXmlParse(entityStr);
                    String return_code = m.get("return_code").toString();
                    if (return_code.equals("SUCCESS")) {
                        String result_code = m.get("result_code").toString();
                        if (result_code.equals("SUCCESS")) {
                            prepayResult = new WechatpayPrepayResult();
                            prepayResult.setAppid(m.get("appid").toString());
                            prepayResult.setMch_id(m.get("mch_id").toString());
                            prepayResult.setDevice_info(m.get("device_info").toString());
                            prepayResult.setNonce_str(m.get("nonce_str").toString());
                            prepayResult.setSign(m.get("sign").toString());
                            prepayResult.setTrade_type(m.get("trade_type").toString());
                            prepayResult.setPrepay_id(m.get("prepay_id").toString());
                            prepayResult.setCode_url(m.get("code_url").toString());
                        } else {
                            throw new RuntimeException("错误" + m.get("err_code").toString() + "\r\n" + m.get("err_code_des").toString());
                        }
                    } else {
                        throw new RuntimeException(m.get("return_msg").toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return prepayResult;
    }

    @Override
    public CallbackResult callback(CallbackParam param) {
        if (param.isPayedSuccessfully() && !finishPayment(param)) {
            return WechatpayCallbackResult.fail("fail to finish payment");
        }

        return WechatpayCallbackResult.success();
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

    private Payment createPayment(CallbackParam param) {
        Payment payment = new Payment();
        payment.setOrderId(Long.valueOf(param.get("out_trade_no")));
        payment.setFinishTime(new Date());
        payment.setPayType(Payment.Type.WECHATPAY);
        payment.setTradeNo(param.get("transaction_id"));
        payment.setFee(Long.valueOf(param.get("total_fee")) / 100.0F);

        return payment;
    }
}
