package cn.momia.service.event.impl;

import cn.momia.api.event.dto.Banner;
import cn.momia.api.event.dto.Event;
import cn.momia.api.event.dto.Icon;
import cn.momia.common.service.AbstractService;
import cn.momia.service.event.EventService;

import java.util.List;

public class EventServiceImpl extends AbstractService implements EventService {
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
