package cn.momia.common.secret;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;

public class PasswordEncryptor {
    public static String encrypt(String mobile, String password, String secretKey) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] encryptedBytes = md5.digest((mobile + "|" + password + "|" + secretKey).getBytes("UTF-8"));

            Base64 base64 = new Base64();
            byte[] encryptedBase64 = base64.encode(encryptedBytes);

            return new String(encryptedBase64);
        } catch (Exception e) {
            throw new RuntimeException("fail to excrypt password of user: " + mobile, e);
        }
    }
}
