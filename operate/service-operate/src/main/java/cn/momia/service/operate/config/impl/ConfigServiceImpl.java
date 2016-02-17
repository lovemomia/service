package cn.momia.service.operate.config.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.operate.config.Config;
import cn.momia.service.operate.config.ConfigService;

import java.util.List;

public class ConfigServiceImpl extends AbstractService implements ConfigService {
    @Override
    public List<Config.Banner> listBanners(int cityId) {
        String sql = "SELECT Cover, Action, Platform, Version FROM SG_Banner WHERE (CityId=? OR CityId=0) AND Status=1 ORDER BY Weight DESC, AddTime DESC";
        return queryObjectList(sql, new Object[] { cityId }, Config.Banner.class);
    }

    @Override
    public List<Config.Icon> listIcons(int cityId) {
        String sql = "SELECT Title, Img, Action, Platform, Version FROM SG_Icon WHERE (CityId=? OR CityId=0) AND Status=1 ORDER BY Weight DESC, AddTime DESC";
        return queryObjectList(sql, new Object[] { cityId }, Config.Icon.class);
    }

    @Override
    public List<Config.Event> listEvents(int cityId) {
        String sql = "SELECT Title, Img, `Desc`, Action, Platform, Version FROM SG_Event WHERE (CityId=? OR CityId=0) AND Status=1 ORDER BY Weight DESC, AddTime DESC";
        return queryObjectList(sql, new Object[] { cityId }, Config.Event.class);
    }
}
