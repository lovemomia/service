package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.common.encrypt.MD5Util;

/**
 * Created by hoze on 15/6/11.
 */
public class MD5SignUtil {

    public static String Sign(String content, String key) {
        String signStr = "";

        if ("" == key) throw new RuntimeException("财付通签名key不能为空！");
        if ("" == content) throw new RuntimeException("财付通签名内容不能为空");

        signStr = content + "&key=" + key;

        return MD5Util.MD5(signStr).toUpperCase();

    }
    public static boolean VerifySignature(String content, String sign, String md5Key) {
        String signStr = content + "&key=" + md5Key;
        String calculateSign = MD5Util.MD5(signStr).toUpperCase();
        String tenpaySign = sign.toUpperCase();
        return (calculateSign == tenpaySign);
    }
}
