package cn.momia.service.deal.gateway.alipay;

import cn.momia.common.service.secret.SecretKey;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AlipayUtil {
    public static String sign(Map<String, String> params, String type) {
        List<String> kvs = new ArrayList<String>();
        String quote = type.equalsIgnoreCase("app") ? "\"" : "";
        kvs.add(AlipayPrepayFields.PARTNER + "=" + quote + params.get(AlipayPrepayFields.PARTNER) + quote);
        kvs.add(AlipayPrepayFields.SELLER_ID + "=" + quote + params.get(AlipayPrepayFields.SELLER_ID) + quote);
        kvs.add(AlipayPrepayFields.OUT_TRADE_NO + "=" + quote + params.get(AlipayPrepayFields.OUT_TRADE_NO) + quote);
        kvs.add(AlipayPrepayFields.SUBJECT + "=" + quote + params.get(AlipayPrepayFields.SUBJECT) + quote);
        kvs.add(AlipayPrepayFields.BODY + "=" + quote + params.get(AlipayPrepayFields.BODY) + quote);
        kvs.add(AlipayPrepayFields.TOTAL_FEE + "=" + quote + params.get(AlipayPrepayFields.TOTAL_FEE) + quote);
        kvs.add(AlipayPrepayFields.NOTIFY_URL + "=" + quote + params.get(AlipayPrepayFields.NOTIFY_URL) + quote);
        kvs.add(AlipayPrepayFields.SERVICE + "=" + quote + params.get(AlipayPrepayFields.SERVICE) + quote);
        kvs.add(AlipayPrepayFields.PAYMENT_TYPE + "=" + quote + params.get(AlipayPrepayFields.PAYMENT_TYPE) + quote);
        kvs.add(AlipayPrepayFields.INPUT_CHARSET + "=" + quote + params.get(AlipayPrepayFields.INPUT_CHARSET) + quote);
        kvs.add(AlipayPrepayFields.IT_B_PAY + "=" + quote + params.get(AlipayPrepayFields.IT_B_PAY) + quote);
        kvs.add(AlipayPrepayFields.SHOW_URL + "=" + quote + params.get(AlipayPrepayFields.SHOW_URL) + quote);

        if (!type.equalsIgnoreCase("app")) Collections.sort(kvs);

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
