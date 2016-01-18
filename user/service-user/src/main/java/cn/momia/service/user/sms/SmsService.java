package cn.momia.service.user.sms;

public interface SmsService {
    boolean sendCode(String mobile);
    boolean verifyCode(String mobile, String code);
    boolean notify(String mobile, String message);
}
