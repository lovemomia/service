package cn.momia.service.event.base.impl;

import cn.momia.api.event.dto.Event;
import cn.momia.common.service.AbstractService;
import cn.momia.service.event.base.EventService;

import java.util.List;

public class EventServiceImpl extends AbstractService implements EventService {
    @Override
    public List<Event> list(int cityId, int count) {
        String sql = "SELECT Title, Img, `Desc`, Action, Platform FROM SG_Event WHERE Status<>0 AND (CityId=? OR CityId=0) ORDER BY Weight DESC, AddTime DESC LIMIT ?";
        return queryObjectList(sql, new Object[] { cityId, count }, Event.class);
    }
}
