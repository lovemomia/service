package cn.momia.service.sms;

public interface SmsSender {
    void send(String mobile, String type);
}
