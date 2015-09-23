package cn.momia.service.feed.base;

import java.util.List;

public interface BaseFeedService {
    boolean isFollowed(long ownUserId, long otherUserId);
    boolean follow(long ownUserId, long otherUserId);

    long add(BaseFeed baseFeed);
    BaseFeed get(long id);
    boolean delete(long userId, long id);

    List<Long> getFollowedIds(long userId);
    long queryFollowedCountByUser(long userId);
    List<BaseFeed> queryFollowedByUser(long userId, int start, int count);
    long queryPublicFeedsCount();
    List<BaseFeed> queryPublicFeeds(int start, int count);

    long queryCountByTopic(long topicId);
    List<BaseFeed> queryByTopic(long topicId, int start, int count);

    void increaseCommentCount(long id);
    void decreaseCommentCount(long id);

    void increaseStarCount(long id);
    void decreaseStarCount(long id);
}
