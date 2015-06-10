package cn.momia.common.sign;

public class Signer {
    public static String sign(String type, String content, String key) {
        if (type.equalsIgnoreCase("RSA")) {
            return RSA.sign(content, key, "utf-8");
        } else {
            throw new RuntimeException("not supported sign type: " + type);
        }
    }
}
