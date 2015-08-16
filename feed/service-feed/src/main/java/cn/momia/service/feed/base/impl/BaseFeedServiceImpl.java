package cn.momia.service.feed.base.impl;

import cn.momia.service.base.impl.DbAccessService;
import cn.momia.service.feed.base.BaseFeed;
import cn.momia.service.feed.base.BaseFeedService;
import cn.momia.service.feed.facade.Feed;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseFeedServiceImpl extends DbAccessService implements BaseFeedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseFeedServiceImpl.class);

    private static final String[] BASE_FEED_FIELDS = { "id", "`type`", "userId", "productId", "topicId", "topic", "content", "lng", "lat", "commentCount", "starCount", "addTime" };

    @Override
    public long add(final BaseFeed baseFeed) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_feed(`type`, userId, productId, topicId, topic, content, lng, lat, addTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, baseFeed.getType());
                ps.setLong(2, baseFeed.getUserId());
                ps.setLong(3, baseFeed.getProductId());
                ps.setLong(4, baseFeed.getTopicId());
                ps.setString(5, baseFeed.getTopic());
                ps.setString(6, baseFeed.getContent());
                ps.setDouble(7, baseFeed.getLng());
                ps.setDouble(8, baseFeed.getLat());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

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
            baseFeed.setProductId(rs.getLong("productId"));
            baseFeed.setTopicId(rs.getLong("topicId"));
            baseFeed.setTopic(rs.getString("topic"));
            baseFeed.setContent(rs.getString("content"));
            baseFeed.setLng(rs.getDouble("lng"));
            baseFeed.setLat(rs.getLong("lat"));
            baseFeed.setCommentCount(rs.getInt("commentCount"));
            baseFeed.setStarCount(rs.getInt("starCount"));
            baseFeed.setAddTime(rs.getTimestamp("addTime"));

            return baseFeed;
        } catch (Exception e) {
            LOGGER.error("fail to build base feed: {}", rs.getLong("id"), e);
            return BaseFeed.INVALID_BASE_FEED;
        }
    }

    @Override
    public boolean delete(long userId, long id) {
        String sql = "UPDATE t_feed SET status=0 WHERE id=? AND userId=?";

        return jdbcTemplate.update(sql, new Object[] { id, userId }) == 1;
    }

    @Override
    public List<Long> getFollowedIds(long id) {
        final List<Long> followedIds = new ArrayList<Long>();
        String sql = "SELECT followedId FROM t_user_follow WHERE userId=? AND status=1";
        jdbcTemplate.query(sql, new Object[] { id }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                followedIds.add(rs.getLong("followedId"));
            }
        });

        return followedIds;
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

        Map<Long, BaseFeed> baseFeedsMap = new HashMap<Long, BaseFeed>();
        for (BaseFeed baseFeed : baseFeeds) baseFeedsMap.put(baseFeed.getId(), baseFeed);
        List<BaseFeed> sortedBaseFeeds = new ArrayList<BaseFeed>();
        for (Long feedId : feedIds) {
            BaseFeed baseFeed = baseFeedsMap.get(feedId);
            if (baseFeed != null) sortedBaseFeeds.add(baseFeed);
        }

        return sortedBaseFeeds;
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

    @Override
    public void increaseCommentCount(long id) {
        String sql = "UPDATE t_feed SET commentCount=CommentCount+1 WHERE id=?";
        jdbcTemplate.update(sql, new Object[] { id });
    }

    @Override
    public void decreaseCommentCount(long id) {
        String sql = "UPDATE t_feed SET commentCount=CommentCount-1 WHERE id=? AND commentCount>=1";
        jdbcTemplate.update(sql, new Object[] { id });
    }

    @Override
    public void increaseStarCount(long id) {
        String sql = "UPDATE t_feed SET starCount=starCount+1 WHERE id=?";
        jdbcTemplate.update(sql, new Object[] { id });
    }

    @Override
    public void decreaseStarCount(long id) {
        String sql = "UPDATE t_feed SET starCount=starCount-1 WHERE id=? AND commentCount>=1";
        jdbcTemplate.update(sql, new Object[] { id });
    }
}
