package cn.momia.service.base.banner.impl;

import cn.momia.service.common.DbAccessService;
import cn.momia.service.base.banner.Banner;
import cn.momia.service.base.banner.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BannerServiceImpl extends DbAccessService implements BannerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BannerServiceImpl.class);

    @Override
    public List<Banner> getBanners(int cityId, int count) {
        final List<Banner> banners = new ArrayList<Banner>();

        try {
            String sql = "SELECT cover, action FROM t_banner WHERE status=1 AND (cityId=? OR cityId=0) ORDER BY addTime DESC LIMIT ?";
            jdbcTemplate.query(sql, new Object[]{cityId, count}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    Banner banner = new Banner();
                    banner.setCover(rs.getString("cover"));
                    banner.setAction(rs.getString("action"));

                    banners.add(banner);
                }
            });
        } catch (Exception e) {
            LOGGER.error("fail to get banners of city: {}", cityId, e);
        }

        return banners;
    }
}
