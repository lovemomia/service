package cn.momia.common.secret;

import cn.momia.common.misc.ValidateUtil;

public class MobileEncryptor {
    public static String encrypt(String mobile) {
        if (ValidateUtil.isInvalidMobile(mobile)) return mobile;
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }
}
