package cn.momia.service.common.sms;

import cn.momia.service.base.Service;

public interface SmsService extends Service {
    boolean sendCode(String mobile, String type);
    boolean verifyCode(String mobile, String code);
    boolean notifyUser(String mobile, String msg);
}
