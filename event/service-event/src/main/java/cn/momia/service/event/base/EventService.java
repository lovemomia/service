package cn.momia.service.event.base;

import java.util.List;

public interface EventService {
    List<Event> list(int cityId, int count);
}
