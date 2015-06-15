package cn.momia.service.sms;

import java.text.ParseException;

public interface SmsSender {
    boolean send(String phone) throws ParseException;
}
