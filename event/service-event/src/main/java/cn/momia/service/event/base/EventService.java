package cn.momia.service.event.base;

import cn.momia.api.event.dto.Event;

import java.util.List;

public interface EventService {
    List<Event> list(int cityId, int count);
}
