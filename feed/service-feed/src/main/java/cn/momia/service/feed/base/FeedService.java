package cn.momia.service.feed.base;

import cn.momia.api.feed.dto.Feed;
import cn.momia.api.feed.dto.FeedTag;

import java.util.Collection;
import java.util.List;

public interface FeedService {
    boolean isFollowed(long ownUserId, long otherUserId);
    boolean follow(long ownUserId, long otherUserId);

    boolean isOfficialUser(long userId);

    long add(Feed feed);
    void push(long feedId, Collection<Long> followedIds);
    Feed get(long feedId);
    boolean delete(long userId, long feedId);

    List<Long> getFollowedIds(long userId);
    long queryFollowedCountByUser(long userId);
    List<Feed> queryFollowedByUser(long userId, int start, int count);
    long queryOfficialFeedsCount();
    List<Feed> queryOfficialFeeds(int start, int count);
    long queryCountByUser(long userId);
    List<Feed> queryByUser(long userId, int start, int count);
    long queryCountBySubject(long subjectId);
    List<Feed> queryBySubject(long subjectId, int start, int count);
    long queryCountByCourse(long courseId);
    List<Feed> queryByCourse(long courseId, int start, int count);

    void increaseCommentCount(long feedId);
    void decreaseCommentCount(long feedId);

    void increaseStarCount(long feedId);
    void decreaseStarCount(long feedId);

    long addTag(long userId, String tagName);
    FeedTag query(String tagName);
    List<FeedTag> listRecommendedTags(int count);
    List<FeedTag> listHotTags(int count);

    List<String> queryLatestImgs(long userId);
}
