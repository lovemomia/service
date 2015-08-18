package cn.momia.service.common.facade;

import cn.momia.service.common.city.City;
import cn.momia.service.common.region.Region;

import java.util.List;

public interface CommonServiceFacade {
    boolean sendCode(String mobile, String type);
    boolean verifyCode(String mobile, String code);
    boolean notifyUser(String mobile, String msg);

    String getCityName(int cityId);
    List<City> getAllCities();
    String gerRegionName(int regionId);
    List<Region> getAllRegions();

    boolean addFeedback(String content, String email);
    boolean addRecommend(String content, String time, String address, String contacts);
}
