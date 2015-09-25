package cn.momia.service.feed.topic.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.feed.topic.FeedTopic;
import cn.momia.service.feed.topic.FeedTopicService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeedTopicServiceImpl extends DbAccessService implements FeedTopicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedTopicServiceImpl.class);

    private static final String[] BASE_FEED_FIELDS = { "id", "type", "refId", "title" };

    @Override
    public FeedTopic get(long id) {
        String sql = "SELECT " + joinFields() + " FROM t_feed_topic WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<FeedTopic>() {
            @Override
            public FeedTopic extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildFeedTopic(rs);
                return FeedTopic.NOT_EXIST_FEED_TOPIC;
            }
        });
    }

    private String joinFields() {
        return StringUtils.join(BASE_FEED_FIELDS, ",");
    }

    private FeedTopic buildFeedTopic(ResultSet rs) throws SQLException {
        try {
            FeedTopic feedTopic = new FeedTopic();
            feedTopic.setId(rs.getLong("id"));
            feedTopic.setType(rs.getInt("type"));
            feedTopic.setRefId(rs.getLong("refId"));
            feedTopic.setTitle(rs.getString("title"));

            return feedTopic;
        } catch (Exception e) {
            LOGGER.error("fail to build feed topic: {}", rs.getLong("id"), e);
            return FeedTopic.NOT_EXIST_FEED_TOPIC;
        }
    }

    @Override
    public List<FeedTopic> list(Collection<Long> ids) {
        final List<FeedTopic> topics = new ArrayList<FeedTopic>();
        String sql = "SELECT " + joinFields() + " FROM t_feed_topic WHERE id IN(" + StringUtils.join(ids, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                FeedTopic feedTopic = buildFeedTopic(rs);
                if (feedTopic.exists()) topics.add(feedTopic);
            }
        });

        return topics;
    }

    @Override
    public long queryCount(int type) {
        String sql = "SELECT COUNT(1) FROM t_feed_topic WHERE type=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { type }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<FeedTopic> query(int type, int start, int count) {
        final List<FeedTopic> topics = new ArrayList<FeedTopic>();
        String sql = "SELECT " + joinFields() + " FROM t_feed_topic WHERE type=? AND status=1 LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { type, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                FeedTopic topic = buildFeedTopic(rs);
                if (topic.exists()) topics.add(topic);
            }
        });

        return topics;
    }
}
