package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.common.encrypt.CommonUtil;
import cn.momia.common.encrypt.XmlUtil;
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

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class WechatPayGateway extends AbstractPaymentGateway {
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
            WechatpayCallbackParam wechatpayCallbackParam = (WechatpayCallbackParam) param;
            orderService.pay(Long.valueOf(wechatpayCallbackParam.getOut_trade_no()));
            // TODO log payment

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
