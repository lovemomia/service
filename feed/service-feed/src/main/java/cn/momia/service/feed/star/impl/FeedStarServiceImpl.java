package cn.momia.service.feed.star.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.feed.star.FeedStarService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeedStarServiceImpl extends AbstractService implements FeedStarService {
    @Override
    public boolean isStared(long userId, long feedId) {
        String sql = "SELECT COUNT(1) FROM SG_FeedStar WHERE UserId=? AND FeedId=? AND Status=1";
        return queryInt(sql, new Object[] { userId, feedId }) > 0;
    }

    @Override
    public List<Long> queryStaredFeeds(long userId, Collection<Long> feedIds) {
        if (feedIds.isEmpty()) return new ArrayList<Long>();

        String sql = "SELECT feedId FROM SG_FeedStar WHERE UserId=? AND FeedId IN(" + StringUtils.join(feedIds, ",") + ") AND Status=1";
        return queryLongList(sql, new Object[] { userId });
    }

    @Override
    public boolean add(long userId, long feedId) {
        long starId = getStarId(userId, feedId);
        if (starId > 0) {
            String sql = "UPDATE SG_FeedStar SET Status=1 WHERE Id=? AND UserId=? AND FeedId=? AND Status=0";
            return update(sql, new Object[] { starId, userId, feedId });
        } else {
            String sql = "INSERT INTO SG_FeedStar(UserId, FeedId, AddTime) VALUES (?, ?, NOW())";
            return update(sql, new Object[] { userId, feedId });
        }
    }

    private long getStarId(long userId, long feedId) {
        String sql = "SELECT Id FROM SG_FeedStar WHERE UserId=? AND FeedId=?";
        return queryLong(sql, new Object[] { userId, feedId });
    }

    @Override
    public boolean delete(long userId, long feedId) {
        String sql = "UPDATE SG_FeedStar SET Status=0 WHERE UserId=? AND FeedId=? AND Status=1";
        return update(sql, new Object[] { userId, feedId });
    }

    @Override
    public int queryUserCount(long feedId) {
        String sql = "SELECT COUNT(DISTINCT UserId) FROM SG_FeedStar WHERE FeedId=? AND Status=1";
        return queryInt(sql, new Object[] { feedId });
    }

    @Override
    public List<Long> queryUserIds(long feedId, int start, int count) {
        String sql = "SELECT UserId FROM SG_FeedStar WHERE FeedId=? AND Status=1 LIMIT ?,?";
        return queryLongList(sql, new Object[] { feedId, start, count });
    }
}
