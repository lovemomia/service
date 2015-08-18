package cn.momia.service.common.sms;

public interface SmsSender {
    boolean send(String mobile, String type);
    boolean notify(String mobile, String msg);
}
