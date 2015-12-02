package cn.momia.service.event.banner;

import cn.momia.api.event.dto.Banner;

import java.util.List;

public interface BannerService {
    List<Banner> list(int cityId, int count);
}
