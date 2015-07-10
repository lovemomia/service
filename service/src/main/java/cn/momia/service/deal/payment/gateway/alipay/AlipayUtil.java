package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.common.web.secret.SecretKey;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AlipayUtil {
    public static List<String> getSignContent(Map<String, String> params) {
        List<String> kvs = new ArrayList<String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equalsIgnoreCase(AlipayPrepayFields.SIGN_TYPE) || key.equalsIgnoreCase(AlipayPrepayFields.SIGN) || StringUtils.isBlank(value))
                continue;
            kvs.add(key + "=" + value);
        }
        Collections.sort(kvs);
        return kvs;
    }

    public static String sign(Map<String, String> params) {
        List<String> kvs = getSignContent(params);

        return RSA.sign(StringUtils.join(kvs, "&"), SecretKey.get("alipayPrivateKey"), "utf-8");
    }

    public static boolean validateSign(Map<String, String> params) {
        List<String> kvs = getSignContent(params);

        String returnedSign = params.get(AlipayCallbackFields.SIGN);

        return RSA.verify(StringUtils.join(kvs, "&"), returnedSign, SecretKey.get("alipayPublicKey"), "utf-8");
    }
}