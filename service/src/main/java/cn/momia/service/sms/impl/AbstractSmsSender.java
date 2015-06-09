package cn.momia.service.sms.impl;

import cn.momia.service.sms.SmsSender;

public abstract class AbstractSmsSender implements SmsSender {
    @Override
    public boolean send(String mobile) {
        return false;
    }
}
