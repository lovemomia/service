package cn.momia.service.base.sms;

public interface SmsSender {
    boolean send(String mobile, String msg);
}
