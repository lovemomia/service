package cn.momia.service.event.base.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.event.base.Event;
import cn.momia.service.event.base.EventService;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventServiceImpl extends DbAccessService implements EventService {
    @Override
    public List<Event> list(int cityId, int count) {
        final List<Event> events = new ArrayList<Event>();
        String sql = "SELECT Title, Img, `Desc`, Action FROM SG_Event WHERE Status=1 AND (CityId=? OR CityId=0) ORDER BY Weight DESC, AddTime DESC LIMIT ?";
        jdbcTemplate.query(sql, new Object[] { cityId, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Event event = new Event();
                event.setTitle(rs.getString("Title"));
                event.setImg(rs.getString("Img"));
                event.setDesc(rs.getString("Desc"));
                event.setAction(rs.getString("Action"));

                events.add(event);
            }
        });

        return events;
    }
}
