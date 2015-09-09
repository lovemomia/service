package cn.momia.service.banner;

import java.util.List;

public interface BannerService {
    List<Banner> list(int cityId, int count);
}
