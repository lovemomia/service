package cn.momia.service.discuss.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.discuss.DiscussReply;
import cn.momia.service.discuss.DiscussService;
import cn.momia.service.discuss.DiscussTopic;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiscussServiceImpl extends AbstractService implements DiscussService {
    @Override
    public int queryTopicCount(int cityId) {
        String sql = "SELECT COUNT(1) FROM SG_DiscussTopic WHERE CityId=? AND Status=1";
        return queryInt(sql, new Object[] { cityId });
    }

    @Override
    public List<DiscussTopic> queryTopics(int cityId, int start, int count) {
        String sql = "SELECT Id FROM SG_DiscussTopic WHERE CityId=? AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        List<Integer> topicIds = queryIntList(sql, new Object[] { cityId, start, count });

        return listTopics(topicIds);
    }

    private List<DiscussTopic> listTopics(Collection<Integer> topicIds) {
        String sql = "SELECT A.Id, A.CityId, A.Cover, A.Title, A.Content, COUNT(DISTINCT B.UserId) AS Joined FROM SG_DiscussTopic A LEFT JOIN SG_DiscussReply B ON A.Id=B.TopicId AND B.Status=1 WHERE A.Id IN (%s) AND A.Status=1 GROUP BY A.Id";
        return listByIds(sql, topicIds, Integer.class, DiscussTopic.class);
    }

    @Override
    public DiscussTopic getTopic(int topicId) {
        List<DiscussTopic> topics = listTopics(Sets.newHashSet(topicId));
        return topics.isEmpty() ? DiscussTopic.NOT_EXIST_DISCUSS_TOPIC : topics.get(0);
    }

    @Override
    public long queryRepliesCount(int topicId) {
        String sql = "SELECT COUNT(1) FROM SG_DiscussReply WHERE TopicId=? AND Status=1";
        return queryLong(sql, new Object[] { topicId });
    }

    @Override
    public List<DiscussReply> queryReplies(int topicId, int start, int count) {
        String sql = "SELECT A.Id, A.TopicId, A.UserId, A.Content, A.AddTime, COUNT(B.Id) AS StaredCount FROM SG_DiscussReply A LEFT JOIN SG_DiscussReplyStar B ON A.Id=B.ReplyId AND B.Status=1 WHERE A.TopicId=? AND A.Status=1 GROUP BY A.Id ORDER BY A.AddTime DESC LIMIT ?,?";
        return queryObjectList(sql, new Object[] { topicId, start, count }, DiscussReply.class);
    }

    @Override
    public boolean reply(long userId, int topicId, String content) {
        String sql = "INSERT INTO SG_DiscussReply (TopicId, UserId, Content, AddTime) VALUES (?, ?, ?, NOW())";
        return update(sql, new Object[] { topicId, userId, content });
    }

    @Override
    public List<Long> filterNotStaredReplyIds(long userId, Collection<Long> replyIds) {
        if (replyIds.isEmpty()) return new ArrayList<Long>();

        String sql = String.format("SELECT ReplyId FROM SG_DiscussReplyStar WHERE UserId=? AND ReplyId IN (%s) AND Status=1", StringUtils.join(replyIds, ","));
        return queryLongList(sql, new Object[] { userId });
    }

    @Override
    public boolean exists(long replyId) {
        String sql = "SELECT COUNT(1) FROM SG_DiscussReply WHERE Id=? AND Status=1";
        return queryInt(sql, new Object[] { replyId }) > 0;
    }

    @Override
    public boolean star(long userId, long replyId) {
        if (isStared(userId, replyId)) return false;

        String sql = "INSERT INTO SG_DiscussReplyStar (ReplyId, UserId, AddTime) VALUES (?, ?, NOW())";
        return update(sql, new Object[] { replyId, userId });
    }

    private boolean isStared(long userId, long replyId) {
        String sql = "SELECT COUNT(1) FROM SG_DiscussReplyStar WHERE ReplyId=? AND UserId=? AND Status=1";
        return queryInt(sql, new Object[] { replyId, userId }) > 0;
    }

    @Override
    public boolean unstar(long userId, long replyId) {
        if (!isStared(userId, replyId)) return false;

        String sql = "UPDATE SG_DiscussReplyStar SET Status=0 WHERE ReplyId=? AND UserId=?";
        return update(sql, new Object[] { replyId, userId });
    }
}
