package cn.momia.service.deal.gateway.wechatpay;

import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.deal.gateway.ClientType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class WechatpayUtil {
    private static final String SIGN = "sign";

    public static String createNoncestr(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            Random rd = new Random();
            builder.append(chars.charAt(rd.nextInt(chars.length() - 1)));
        }

        return builder.toString();
    }

    public static String sign(Map<String, String> params, int clientType) {
        List<String> kvs = new ArrayList<String>();
        for (Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equalsIgnoreCase(SIGN) || StringUtils.isBlank(value)) continue;
            kvs.add(key + "=" + value);
        }
        Collections.sort(kvs);
        kvs.add("key=" + (ClientType.isFromApp(clientType) ? Configuration.getString("Payment.Wechat.AppKey") : Configuration.getString("Payment.Wechat.JsApiKey")));

        String s = StringUtils.join(kvs, "&");
        return DigestUtils.md5Hex(s).toUpperCase();
    }

    public static boolean validateSign(Map<String, String> params, int clientType) {
        String returnedSign = params.get(SIGN);
        String generatedSign = sign(params, clientType);

        return generatedSign.equals(returnedSign);
    }
}
