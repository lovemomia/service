package cn.momia.service.feed.star;

import cn.momia.common.service.Service;

import java.util.List;

public interface FeedStarService extends Service {
    boolean add(long userId, long feedId);
    boolean delete(long userId, long feedId);

    int queryUserCount(long feedId);
    List<Long> queryUserIds(long id, int start, int count);
}
