package cn.momia.service.feed.base;

import java.util.List;

public interface BaseFeedService {
    long add(BaseFeed baseFeed);
    BaseFeed get(long id);
    boolean delete(long userId, long id);

    boolean isFollowed(long userId, long followedId);
    boolean follow(long userId, long followId);
    List<Long> getFollowedIds(long id);
    long queryFollowedCountByUser(long userId);
    List<BaseFeed> queryFollowedByUser(long userId, int start, int count);

    long queryCountByTopic(long topicId);
    List<BaseFeed> queryByTopic(long topicId, int start, int count);

    void increaseCommentCount(long id);
    void decreaseCommentCount(long id);

    void increaseStarCount(long id);
    void decreaseStarCount(long id);
}
