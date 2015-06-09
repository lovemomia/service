package cn.momia.service.sms;

public interface SmsVerifier {
    boolean send(String mobile);
    boolean verify(String mobile, String verifyCode);
}
