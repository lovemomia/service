package cn.momia.service.feed.star;

import java.util.Collection;
import java.util.List;

public interface FeedStarService {
    boolean isStared(long userId, long feedId);
    List<Long> queryStaredFeeds(long userId, Collection<Long> feedIds);

    boolean add(long userId, long feedId);
    boolean delete(long userId, long feedId);

    int queryUserCount(long feedId);
    List<Long> queryUserIds(long id, int start, int count);
}
