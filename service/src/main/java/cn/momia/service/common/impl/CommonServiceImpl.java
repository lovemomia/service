package cn.momia.service.common.impl;

import cn.momia.common.misc.ValidateUtil;
import cn.momia.service.common.CommonService;
import cn.momia.service.common.sms.SmsSender;
import cn.momia.service.common.sms.SmsVerifier;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class CommonServiceImpl implements CommonService {
    private static final Set<String> TYPES = Sets.newHashSet(new String[] { "register", "login" });

    private SmsSender smsSender;
    private SmsVerifier smsVerifier;

    public void setSmsSender(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    public void setSmsVerifier(SmsVerifier smsVerifier) {
        this.smsVerifier = smsVerifier;
    }

    @Override
    public boolean sendCode(String mobile, String type) {
        if (ValidateUtil.isInvalidMobile(mobile) || isInvalidType(type)) return false;

        return smsSender.send(mobile, type);
    }

    private boolean isInvalidType(String type) {
        return !TYPES.contains(type);
    }

    @Override
    public boolean verifyCode(String mobile, String code) {
        if (ValidateUtil.isInvalidMobile(mobile) || StringUtils.isBlank(code)) return false;

        return smsVerifier.verify(mobile, code);
    }
}
