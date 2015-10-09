package cn.momia.service.event.banner;

import java.util.List;

public interface BannerService {
    List<Banner> list(int cityId, int count);
}
