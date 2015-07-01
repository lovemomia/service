package cn.momia.service.sms;

import cn.momia.service.sms.impl.SmsLoginException;

public interface SmsSender {
    void send(String mobile, String type) throws SmsLoginException;
}
