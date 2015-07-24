package cn.momia.service.feed.base.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.feed.base.BaseFeed;
import cn.momia.service.feed.base.BaseFeedService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseFeedServiceImpl extends DbAccessService implements BaseFeedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseFeedServiceImpl.class);

    private static final String[] BASE_FEED_FIELDS = { "id", "type", "userId", "topicId", "content", "lng", "lat", "addTime" };

    @Override
    public BaseFeed get(long id) {
        String sql = "SELECT " + joinFields() + " FROM t_feed WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<BaseFeed>() {
            @Override
            public BaseFeed extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildBaseFeed(rs);
                return BaseFeed.NOT_EXIST_BASE_FEED;
            }
        });
    }

    private String joinFields() {
        return StringUtils.join(BASE_FEED_FIELDS, ",");
    }

    private BaseFeed buildBaseFeed(ResultSet rs) throws SQLException {
        try {
            BaseFeed baseFeed = new BaseFeed();
            baseFeed.setId(rs.getLong("id"));
            baseFeed.setType(rs.getInt("type"));
            baseFeed.setUserId(rs.getLong("userId"));
            baseFeed.setTopicId(rs.getLong("topicId"));
            baseFeed.setContent(rs.getString("content"));
            baseFeed.setLng(rs.getDouble("lng"));
            baseFeed.setLat(rs.getLong("lat"));
            baseFeed.setAddTime(rs.getTimestamp("addTime"));

            return baseFeed;
        } catch (Exception e) {
            LOGGER.error("fail to build base feed: {}", rs.getLong("id"), e);
            return BaseFeed.INVALID_BASE_FEED;
        }
    }
}
