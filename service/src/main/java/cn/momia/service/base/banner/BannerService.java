package cn.momia.service.base.banner;

import java.util.List;

public interface BannerService {
    List<Banner> getBanners(int cityId, int count);
}
