package cn.momia.service.user.sms;

import java.util.Collection;

public interface SmsService {
    boolean sendCode(String mobile);
    boolean verifyCode(String mobile, String code);

    boolean notify(String mobile, String message);
    boolean notify(Collection<String> mobiles, String message);
}
