package cn.momia.service.operate.config.impl;

import cn.momia.api.operate.dto.Banner;
import cn.momia.api.operate.dto.Event;
import cn.momia.api.operate.dto.Icon;
import cn.momia.common.service.AbstractService;
import cn.momia.service.operate.config.ConfigService;

import java.util.List;

public class ConfigServiceImpl extends AbstractService implements ConfigService {
    @Override
    public List<Banner> listBanners(int cityId) {
        String sql = "SELECT Cover, Action, Platform, Version FROM SG_Banner WHERE Status<>0 AND (CityId=? OR CityId=0) ORDER BY Weight DESC, AddTime DESC";
        return queryObjectList(sql, new Object[] { cityId }, Banner.class);
    }

    @Override
    public List<Icon> listIcons(int cityId) {
        String sql = "SELECT Title, Img, Action, Platform, Version FROM SG_Icon WHERE (CityId=? OR CityId=0) AND Status<>0 ORDER BY Weight DESC, AddTime DESC";
        return queryObjectList(sql, new Object[] { cityId }, Icon.class);
    }

    @Override
    public List<Event> listEvents(int cityId) {
        String sql = "SELECT Title, Img, `Desc`, Action, Platform, Version FROM SG_Event WHERE Status<>0 AND (CityId=? OR CityId=0) ORDER BY Weight DESC, AddTime DESC";
        return queryObjectList(sql, new Object[] { cityId }, Event.class);
    }
}
