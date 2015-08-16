package cn.momia.service.common.facade.impl;

import cn.momia.service.base.util.MobileUtil;
import cn.momia.api.base.exception.MomiaFailedException;
import cn.momia.service.common.facade.CommonServiceFacade;
import cn.momia.service.common.city.City;
import cn.momia.service.common.city.CityService;
import cn.momia.service.common.feedback.FeedbackService;
import cn.momia.service.common.recommend.RecommendService;
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
    private RecommendService recommendService;

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

    public void setRecommendService(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @Override
    public boolean sendCode(String mobile, String type) {
        if (MobileUtil.isInvalidMobile(mobile) || isInvalidType(type)) return false;

        return smsSender.send(mobile, type);
    }

    private boolean isInvalidType(String type) {
        return !TYPES.contains(type);
    }

    @Override
    public boolean verifyCode(String mobile, String code) {
        if (MobileUtil.isInvalidMobile(mobile) || StringUtils.isBlank(code)) return false;

        return smsVerifier.verify(mobile, code);
    }

    @Override
    public String getCityName(int cityId) {
        return cityService.get(cityId).getName();
    }

    @Override
    public List<City> getAllCities() {
        return cityService.getAll();
    }

    @Override
    public String gerRegionName(int regionId) {
        return regionService.get(regionId).getName();
    }

    @Override
    public List<Region> getAllRegions() {
        return regionService.getAll();
    }

    @Override
    public boolean addFeedback(String content, String email) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(email)) return false;
        if (content.length() > 480) throw new MomiaFailedException("反馈意见字数超出限制");

        return feedbackService.add(content, email) > 0;
    }

    @Override
    public boolean addRecommend(String content, String time, String address, String contacts) {
        if (StringUtils.isBlank(content) ||
                StringUtils.isBlank(time) ||
                StringUtils.isBlank(address) ||
                StringUtils.isBlank(contacts)) return false;
        if (content.length() > 600) throw new MomiaFailedException("爆料字数超出限制");

        return recommendService.add(content, time, address, contacts) > 0;
    }
}
