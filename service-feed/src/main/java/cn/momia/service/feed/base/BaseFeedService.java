package cn.momia.service.feed.base;

import cn.momia.common.service.Service;

import java.util.List;

public interface BaseFeedService extends Service {
    BaseFeed get(long id);

    long queryFollowedCountByUser(long userId);
    List<BaseFeed> queryFollowedByUser(long userId, int start, int count);

    long queryCountByTopic(long topicId);
    List<BaseFeed> queryByTopic(long topicId, int start, int count);

    void increaseCommentCount(long id);
    void decreaseCommentCount(long id);

    void increaseStarCount(long id);
    void decreaseStarCount(long id);
}
