package cn.momia.service.feed.base.impl;

import cn.momia.common.service.impl.DbAccessService;
import cn.momia.service.feed.base.BaseFeed;
import cn.momia.service.feed.base.BaseFeedService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public long queryFollowedCountByUser(long userId) {
        String sql = "SELECT COUNT(1) FROM t_feed_follow WHERE userId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { userId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<BaseFeed> queryFollowedByUser(long userId, int start, int count) {
        final List<Long> feedIds = new ArrayList<Long>();
        String queryIdsSql = "SELECT feedId FROM t_feed_follow WHERE userId=? AND status=1 ORDER BY addTime DESC LIMIT ?,?";
        jdbcTemplate.query(queryIdsSql, new Object[] { userId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                feedIds.add(rs.getLong("feedId"));
            }
        });

        if (feedIds.isEmpty()) return new ArrayList<BaseFeed>();

        final List<BaseFeed> baseFeeds = new ArrayList<BaseFeed>();
        String sql = "SELECT " + joinFields() + " FROM t_feed WHERE id IN(" + StringUtils.join(feedIds, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                BaseFeed baseFeed = buildBaseFeed(rs);
                if (baseFeed.exists()) baseFeeds.add(baseFeed);
            }
        });

        return baseFeeds;
    }

    @Override
    public long queryCountByTopic(long topicId) {
        String sql = "SELECT COUNT(1) FROM t_feed WHERE topicId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { topicId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<BaseFeed> queryByTopic(long topicId, int start, int count) {
        final List<BaseFeed> baseFeeds = new ArrayList<BaseFeed>();
        String sql = "SELECT " + joinFields() + " FROM t_feed WHERE topicId=? AND status=1 ORDER BY addTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { topicId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                BaseFeed baseFeed = buildBaseFeed(rs);
                if (baseFeed.exists()) baseFeeds.add(baseFeed);
            }
        });

        return baseFeeds;
    }
}
