package cn.momia.service.discuss;

import java.util.Collection;
import java.util.List;

public interface DiscussService {
    int queryTopicCount(int cityId);
    List<DiscussTopic> queryTopics(int cityId, int start, int count);

    DiscussTopic getTopic(int topicId);

    long queryRepliesCount(int topicId);
    List<DiscussReply> queryReplies(int topicId, int start, int count);

    boolean reply(long userId, int topicId, String content);

    List<Long> filterNotStaredReplyIds(long userId, Collection<Long> replyIds);

    boolean star(long userId, int replyId);
    boolean unstar(long userId, int replyId);

}
