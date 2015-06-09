package cn.momia.service.sms.impl;

import cn.momia.service.sms.SmsVerifier;

public abstract class AbstractSmsVerifier implements SmsVerifier {
    @Override
    public boolean send(String mobile) {
        return false;
    }

    @Override
    public boolean verify(String mobile, String verifyCode) {
        return false;
    }
}
