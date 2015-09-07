package cn.momia.service.common.sms;

public interface SmsService {
    boolean sendCode(String mobile, String type);
    boolean verifyCode(String mobile, String code);
    boolean notifyUser(String mobile, String msg);
}
