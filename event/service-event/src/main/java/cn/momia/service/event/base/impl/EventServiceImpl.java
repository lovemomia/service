package cn.momia.service.event.base.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.event.base.Event;
import cn.momia.service.event.base.EventService;

import java.util.List;

public class EventServiceImpl extends DbAccessService implements EventService {
    @Override
    public List<Event> list(int cityId, int count) {
        String sql = "SELECT * FROM SG_Event WHERE Status=1 AND (CityId=? OR CityId=0) ORDER BY Weight DESC, AddTime DESC LIMIT ?";
        return queryList(sql, new Object[] { cityId, count }, Event.class);
    }
}
