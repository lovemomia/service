package cn.momia.service.event.banner.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.event.banner.Banner;
import cn.momia.service.event.banner.BannerService;

import java.util.List;

public class BannerServiceImpl extends AbstractService implements BannerService {
    @Override
    public List<Banner> list(int cityId, int count) {
        String sql = "SELECT Cover, Action FROM SG_Banner WHERE Status=1 AND (CityId=? OR CityId=0) ORDER BY Weight DESC, AddTime DESC LIMIT ?";
        return queryObjectList(sql, new Object[] { cityId, count }, Banner.class);
    }
}
