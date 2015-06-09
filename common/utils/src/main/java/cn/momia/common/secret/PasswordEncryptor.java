package cn.momia.common.secret;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;

public class PasswordEncryptor {
    public static String encrypt(String username, String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] encryptedBytes = md5.digest(((username.length() >= 20 ? username.substring(0, 20) : String.format("%0" + (20 - username.length()) + "d", 0) + username) + "|" + password).getBytes("UTF-8"));

            Base64 base64 = new Base64();
            byte[] encryptedBase64 = base64.encode(encryptedBytes);

            return new String(encryptedBase64);
        } catch (Exception e) {
            throw new RuntimeException("fail to excrypt password of cn.momia.service.base.user: " + username, e);
        }
    }
}
