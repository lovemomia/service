package cn.momia.service.product.banner;

import cn.momia.service.base.Service;

import java.util.List;

public interface BannerService extends Service {
    List<Banner> getBanners(int cityId, int count);
}
