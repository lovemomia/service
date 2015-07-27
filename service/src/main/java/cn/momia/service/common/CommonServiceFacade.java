package cn.momia.service.common;

import cn.momia.service.common.city.City;
import cn.momia.service.common.region.Region;

import java.util.List;

public interface CommonServiceFacade {
    boolean sendCode(String mobile, String type);
    boolean verifyCode(String mobile, String code);

    String getCityName(int cityId);
    List<City> getAllCities();
    String gerRegionName(int regionId);
    List<Region> getAllRegions();

    long addFeedback(String content, String email);
}
