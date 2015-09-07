package cn.momia.service.product.banner;

import java.util.List;

public interface BannerService {
    List<Banner> getBanners(int cityId, int count);
}
