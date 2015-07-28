package cn.momia.service.feed.star;

import cn.momia.common.service.Service;

import java.util.List;

public interface FeedStarService extends Service {
    int queryUserCount(long feedId);
    List<Long> queryUserIds(long id, int start, int count);
}
