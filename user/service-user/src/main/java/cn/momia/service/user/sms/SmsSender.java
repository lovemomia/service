package cn.momia.service.user.sms;

public interface SmsSender {
    boolean send(String mobile, String message);
}
