package cn.momia.service.promo.banner;

import cn.momia.common.service.Service;

import java.util.List;

public interface BannerService extends Service {
    List<Banner> getBanners(int cityId, int count);
}
