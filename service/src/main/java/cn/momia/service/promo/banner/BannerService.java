package cn.momia.service.promo.banner;

import java.util.List;

public interface BannerService {
    List<Banner> getBanners(int cityId, int count);
}
