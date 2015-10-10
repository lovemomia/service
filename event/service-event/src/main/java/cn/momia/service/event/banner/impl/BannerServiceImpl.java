package cn.momia.service.event.banner.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.event.banner.Banner;
import cn.momia.service.event.banner.BannerService;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BannerServiceImpl extends DbAccessService implements BannerService {
    @Override
    public List<Banner> list(int cityId, int count) {
        final List<Banner> banners = new ArrayList<Banner>();
        String sql = "SELECT Cover, Action FROM SG_Banner WHERE Status=1 AND (CityId=? OR CityId=0) ORDER BY Weight DESC, AddTime DESC LIMIT ?";
        jdbcTemplate.query(sql, new Object[] { cityId, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Banner banner = new Banner();
                banner.setCover(rs.getString("Cover"));
                banner.setAction(rs.getString("Action"));

                banners.add(banner);
            }
        });

        return banners;
    }
}
