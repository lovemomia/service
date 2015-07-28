package cn.momia.service.feed.topic.impl;

import cn.momia.common.service.impl.DbAccessService;
import cn.momia.service.feed.topic.FeedTopic;
import cn.momia.service.feed.topic.FeedTopicService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedTopicServiceImpl extends DbAccessService implements FeedTopicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedTopicServiceImpl.class);

    private static final String[] BASE_FEED_FIELDS = { "id", "title", "productId" };

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
            feedTopic.setTitle(rs.getString("title"));
            feedTopic.setProductId(rs.getLong("productId"));

            return feedTopic;
        } catch (Exception e) {
            LOGGER.error("fail to build feed topic: {}", rs.getLong("id"), e);
            return FeedTopic.INVALID_FEED_TOPIC;
        }
    }
}
