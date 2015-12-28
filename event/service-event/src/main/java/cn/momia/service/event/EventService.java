package cn.momia.service.event;

import cn.momia.api.event.dto.Banner;
import cn.momia.api.event.dto.Event;
import cn.momia.api.event.dto.Icon;

import java.util.List;

public interface EventService {
    List<Banner> listBanners(int cityId);
    List<Icon> listIcons(int cityId);
    List<Event> listEvents(int cityId);
}
