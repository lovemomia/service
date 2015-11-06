package cn.momia.service.feed.base;

import java.util.Collection;
import java.util.List;

public interface FeedService {
    boolean isFollowed(long ownUserId, long otherUserId);
    boolean follow(long ownUserId, long otherUserId);

    long add(Feed feed);
    void push(long feedId, Collection<Long> followedIds);
    Feed get(long feedId);
    boolean delete(long userId, long feedId);

    List<Long> getFollowedIds(long userId);
    long queryFollowedCountByUser(long userId);
    List<Feed> queryFollowedByUser(long userId, int start, int count);
    long queryOfficialFeedsCount();
    List<Feed> queryOfficialFeeds(int start, int count);
    long queryCountByCourse(long courseId);
    List<Feed> queryByCourse(long courseId, int start, int count);

    void increaseCommentCount(long feedId);
    void decreaseCommentCount(long feedId);

    void increaseStarCount(long feedId);
    void decreaseStarCount(long feedId);
}
