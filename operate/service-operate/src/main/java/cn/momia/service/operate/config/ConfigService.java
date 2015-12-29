package cn.momia.service.operate.config;

import cn.momia.api.operate.dto.Banner;
import cn.momia.api.operate.dto.Event;
import cn.momia.api.operate.dto.Icon;

import java.util.List;

public interface ConfigService {
    List<Banner> listBanners(int cityId);
    List<Icon> listIcons(int cityId);
    List<Event> listEvents(int cityId);
}
