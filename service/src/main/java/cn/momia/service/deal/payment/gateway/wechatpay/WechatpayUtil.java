package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.common.web.secret.SecretKey;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class WechatpayUtil {
    public static String createNoncestr(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < length; i++) {
            Random rd = new Random();
            res += chars.indexOf(rd.nextInt(chars.length() - 1));
        }

        return res;
    }

    public static String sign(PrepayParam param, String tradeType) {
        List<String> kvs = new ArrayList<String>();
        for (Entry<String, String> entry : param.getAll().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equalsIgnoreCase(WechatpayPrepayFields.SIGN) || StringUtils.isBlank(value)) continue;
            kvs.add(key + "=" + value);
        }
        Collections.sort(kvs);
        kvs.add("key=" + (tradeType.equalsIgnoreCase("NATIVE") ? SecretKey.get("wechatpayNative") : SecretKey.get("wechatpayJsApi")));

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return new String(md5.digest(StringUtils.join(kvs, "&").getBytes())).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
