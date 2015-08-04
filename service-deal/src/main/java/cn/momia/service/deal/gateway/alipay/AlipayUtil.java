package cn.momia.service.deal.gateway.alipay;

import cn.momia.common.service.config.Configuration;
import cn.momia.common.service.exception.MomiaFailedException;
import cn.momia.service.deal.gateway.ClientType;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AlipayUtil {
    public static String sign(Map<String, String> params, int clientType) {
        List<String> kvs = new ArrayList<String>();
        String quote = ClientType.isFromApp(clientType) ? "\"" : "";
        kvs.add(AlipayPrepayResult.Field.PARTNER + "=" + quote + params.get(AlipayPrepayResult.Field.PARTNER) + quote);
        kvs.add(AlipayPrepayResult.Field.SELLER_ID + "=" + quote + params.get(AlipayPrepayResult.Field.SELLER_ID) + quote);
        kvs.add(AlipayPrepayResult.Field.OUT_TRADE_NO + "=" + quote + params.get(AlipayPrepayResult.Field.OUT_TRADE_NO) + quote);
        kvs.add(AlipayPrepayResult.Field.SUBJECT + "=" + quote + params.get(AlipayPrepayResult.Field.SUBJECT) + quote);
        kvs.add(AlipayPrepayResult.Field.BODY + "=" + quote + params.get(AlipayPrepayResult.Field.BODY) + quote);
        kvs.add(AlipayPrepayResult.Field.TOTAL_FEE + "=" + quote + params.get(AlipayPrepayResult.Field.TOTAL_FEE) + quote);
        kvs.add(AlipayPrepayResult.Field.NOTIFY_URL + "=" + quote + params.get(AlipayPrepayResult.Field.NOTIFY_URL) + quote);
        kvs.add(AlipayPrepayResult.Field.SERVICE + "=" + quote + params.get(AlipayPrepayResult.Field.SERVICE) + quote);
        kvs.add(AlipayPrepayResult.Field.PAYMENT_TYPE + "=" + quote + params.get(AlipayPrepayResult.Field.PAYMENT_TYPE) + quote);
        kvs.add(AlipayPrepayResult.Field.INPUT_CHARSET + "=" + quote + params.get(AlipayPrepayResult.Field.INPUT_CHARSET) + quote);
        kvs.add(AlipayPrepayResult.Field.IT_B_PAY + "=" + quote + params.get(AlipayPrepayResult.Field.IT_B_PAY) + quote);
        kvs.add(AlipayPrepayResult.Field.SHOW_URL + "=" + quote + params.get(AlipayPrepayResult.Field.SHOW_URL) + quote);
        if (ClientType.isFromWap(clientType)) {
            kvs.add(AlipayPrepayResult.Field.RETURN_URL + "=" + quote + params.get(AlipayPrepayResult.Field.RETURN_URL) + quote);
            Collections.sort(kvs);
        }

        try {
            return URLEncoder.encode(RSA.sign(StringUtils.join(kvs, "&"), Configuration.getSecretKey("alipayPrivateKey"), "utf-8"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new MomiaFailedException("unsupported encoding", e);
        }
    }

    public static boolean validateSign(Map<String, String> params, String sign) {
        List<String> kvs = new ArrayList<String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equalsIgnoreCase(AlipayPrepayResult.Field.SIGN_TYPE) || key.equalsIgnoreCase(AlipayPrepayResult.Field.SIGN) || StringUtils.isBlank(value)) continue;
            kvs.add(key + "=" + value);
        }
        Collections.sort(kvs);

        return RSA.verify(StringUtils.join(kvs, "&"), sign, Configuration.getSecretKey("alipayPublicKey"), "utf-8");
    }
}
