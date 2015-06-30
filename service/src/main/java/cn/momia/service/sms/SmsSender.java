package cn.momia.service.sms;

import cn.momia.service.sms.impl.MyException;

public interface SmsSender {
    void send(String mobile, String type) throws MyException;
}
