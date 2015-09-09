package cn.momia.service.topic.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.topic.Topic;
import cn.momia.service.topic.TopicGroup;
import cn.momia.service.topic.TopicService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopicServiceImpl extends DbAccessService implements TopicService {
    @Override
    public Topic get(long id) {
        if (id <= 0) return Topic.NOT_EXIST_TOPIC;

        String sql = "SELECT id, cover, title FROM t_topic WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Topic>() {
            @Override
            public Topic extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildTopic(rs);
                return Topic.NOT_EXIST_TOPIC;
            }
        });
    }

    private Topic buildTopic(ResultSet rs) throws SQLException {
        Topic topic = new Topic();
        topic.setId(rs.getLong("id"));
        topic.setCover(rs.getString("cover"));
        topic.setTitle(rs.getString("title"));

        return topic;
    }

    @Override
    public List<TopicGroup> listTopicGroups(long id) {
        if (id <= 0) return new ArrayList<TopicGroup>();

        final List<TopicGroup> topicGroups = new ArrayList<TopicGroup>();
        String sql = "SELECT id, topicId, title FROM t_topic_group WHERE topicId=? AND status=1 ORDER BY ordinal DESC, addTime ASC";
        jdbcTemplate.query(sql, new Object[] { id }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                topicGroups.add(buildTopicGroup(rs));
            }
        });

        return topicGroups;
    }

    private TopicGroup buildTopicGroup(ResultSet rs) throws SQLException {
        TopicGroup topicGroup = new TopicGroup();
        topicGroup.setId(rs.getLong("id"));
        topicGroup.setTopicId(rs.getLong("topicId"));
        topicGroup.setTitle(rs.getString("title"));

        return topicGroup;
    }

    @Override
    public Map<Long, List<Long>> queryProductIds(List<Long> groupIds) {
        if (groupIds.isEmpty()) return new HashMap<Long, List<Long>>();

        final Map<Long, List<Long>> groupedProductIds = new HashMap<Long, List<Long>>();
        String sql = "SELECT groupId, productId FROM t_topic_group_product WHERE groupId IN(" + StringUtils.join(groupIds, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long groupId = rs.getLong("groupId");
                long productId = rs.getLong("productId");
                List<Long> productIds = groupedProductIds.get(groupId);
                if (productIds == null) {
                    productIds = new ArrayList<Long>();
                    groupedProductIds.put(groupId, productIds);
                }
                productIds.add(productId);
            }
        });

        return groupedProductIds;
    }
}
