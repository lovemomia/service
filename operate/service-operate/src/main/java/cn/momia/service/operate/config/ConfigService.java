package cn.momia.service.operate.config;

import java.util.List;

public interface ConfigService {
    List<Config.Banner> listBanners(int cityId);
    List<Config.Icon> listIcons(int cityId);
    List<Config.Event> listEvents(int cityId);
}
