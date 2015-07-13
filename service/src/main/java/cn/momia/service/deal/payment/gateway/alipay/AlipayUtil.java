package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.common.web.secret.SecretKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AlipayUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayUtil.class);

    public static String sign(Map<String, String> params) {
        List<String> kvs = new ArrayList<String>();
        kvs.add(AlipayPrepayFields.PARTNER + "=\"" + params.get(AlipayPrepayFields.PARTNER) + "\"");
        kvs.add(AlipayPrepayFields.SELLER_ID + "=\"" + params.get(AlipayPrepayFields.SELLER_ID) + "\"");
        kvs.add(AlipayPrepayFields.OUT_TRADE_NO + "=\"" + params.get(AlipayPrepayFields.OUT_TRADE_NO) + "\"");
        kvs.add(AlipayPrepayFields.SUBJECT + "=\"" + params.get(AlipayPrepayFields.SUBJECT) + "\"");
        kvs.add(AlipayPrepayFields.BODY + "=\"" + params.get(AlipayPrepayFields.BODY) + "\"");
        kvs.add(AlipayPrepayFields.TOTAL_FEE + "=\"" + params.get(AlipayPrepayFields.TOTAL_FEE) + "\"");
        kvs.add(AlipayPrepayFields.NOTIFY_URL + "=\"" + params.get(AlipayPrepayFields.NOTIFY_URL) + "\"");
        kvs.add(AlipayPrepayFields.SERVICE + "=\"" + params.get(AlipayPrepayFields.SERVICE) + "\"");
        kvs.add(AlipayPrepayFields.PAYMENT_TYPE + "=\"" + params.get(AlipayPrepayFields.PAYMENT_TYPE) + "\"");
        kvs.add(AlipayPrepayFields.INPUT_CHARSET + "=\"" + params.get(AlipayPrepayFields.INPUT_CHARSET) + "\"");
        kvs.add(AlipayPrepayFields.IT_B_PAY + "=\"" + params.get(AlipayPrepayFields.IT_B_PAY) + "\"");
        kvs.add(AlipayPrepayFields.SHOW_URL + "=\"" + params.get(AlipayPrepayFields.SHOW_URL) + "\"");

        try {
            return URLEncoder.encode(RSA.sign(StringUtils.join(kvs, "&"), SecretKey.get("alipayPrivateKey"), "utf-8"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean validateSign(Map<String, String> params) {
        List<String> kvs = new ArrayList<String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equalsIgnoreCase(AlipayPrepayFields.SIGN_TYPE) || key.equalsIgnoreCase(AlipayPrepayFields.SIGN) || StringUtils.isBlank(value)) continue;
            kvs.add(key + "=" + value);
        }
        Collections.sort(kvs);

        String returnedSign = params.get(AlipayCallbackFields.SIGN);

        return RSA.verify(StringUtils.join(kvs, "&"), returnedSign, SecretKey.get("alipayPublicKey"), "utf-8");
    }

}
