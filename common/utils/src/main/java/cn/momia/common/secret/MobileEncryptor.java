package cn.momia.common.secret;

public class MobileEncryptor {
    public static String encrypt(String mobile) {
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }
}
