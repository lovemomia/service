package cn.momia.service.common.impl;

import cn.momia.common.misc.ValidateUtil;
import cn.momia.service.common.CommonServiceFacade;
import cn.momia.service.common.city.City;
import cn.momia.service.common.city.CityService;
import cn.momia.service.common.feedback.FeedbackService;
import cn.momia.service.common.region.Region;
import cn.momia.service.common.region.RegionService;
import cn.momia.service.common.sms.SmsSender;
import cn.momia.service.common.sms.SmsVerifier;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

public class CommonServiceFacadeImpl implements CommonServiceFacade {
    private static final Set<String> TYPES = Sets.newHashSet(new String[] { "register", "login" });

    private SmsSender smsSender;
    private SmsVerifier smsVerifier;
    private CityService cityService;
    private RegionService regionService;
    private FeedbackService feedbackService;

    public void setSmsSender(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    public void setSmsVerifier(SmsVerifier smsVerifier) {
        this.smsVerifier = smsVerifier;
    }

    public void setCityService(CityService cityService) {
        this.cityService = cityService;
    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void setFeedbackService(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
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

    @Override
    public List<City> getAllCities() {
        return cityService.getAll();
    }

    @Override
    public List<Region> getAllRegions() {
        return regionService.getAll();
    }

    @Override
    public long addFeedback(String content, String email) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(email)) return 0;
        return feedbackService.add(content, email);
    }
}
