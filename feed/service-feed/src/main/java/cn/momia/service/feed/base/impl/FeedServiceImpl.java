package cn.momia.service.feed.base.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.feed.base.Feed;
import cn.momia.service.feed.base.FeedService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeedServiceImpl extends DbAccessService implements FeedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedServiceImpl.class);

    @Override
    public boolean isFollowed(long ownUserId, long otherUserId) {
        String sql = "SELECT COUNT(1) FROM SG_UserFollow WHERE UserId=? AND FollowedId=? AND Status=1";
        return queryInt(sql, new Object[] { ownUserId, otherUserId }) > 0;
    }

    @Override
    public boolean follow(long ownUserId, long otherUserId) {
        String sql = "INSERT INTO SG_UserFollow(UserId, FollowedId, AddTime) VALUES (?, ?, NOW())";
        return update(sql, new Object[] { ownUserId, otherUserId });
    }

    @Override
    public long add(final Feed feed) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_Feed(`Type`, UserId, Content, CourseId, CourseTitle, Lng, Lat, AddTime) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, feed.getType());
                ps.setLong(2, feed.getUserId());
                ps.setString(3, feed.getContent());
                ps.setLong(4, feed.getCourseId());
                ps.setString(5, feed.getCourseTitle());
                ps.setDouble(6, feed.getLng());
                ps.setDouble(7, feed.getLat());

                return ps;
            }
        }, keyHolder);

        long feedId = keyHolder.getKey().longValue();
        if (feedId > 0) addFeedImgs(feedId, feed.getImgs());

        return feedId;
    }

    private void addFeedImgs(long feedId, List<String> imgs) {
        try {
            String sql = "INSERT INTO SG_FeedImg(FeedId, Url, AddTime) VALUES (?, ?, NOW())";
            List<Object[]> args = new ArrayList<Object[]>();
            for (String img : imgs) {
                args.add(new Object[] { feedId, img });
            }
            jdbcTemplate.batchUpdate(sql, args);
        } catch (Exception e) {
            LOGGER.error("fail to add image for feed: {}", feedId, e);
        }
    }

    @Override
    public void push(long feedId, Collection<Long> followedIds) {
        String sql = "INSERT INTO SG_FeedFollow(UserId, FeedId, AddTime) VALUES (?, ?, NOW())";
        List<Object[]> args = new ArrayList<Object[]>();
        for (long followedId : followedIds) {
            args.add(new Object[] { followedId, feedId });
        }
        jdbcTemplate.batchUpdate(sql, args);
    }

    @Override
    public Feed get(long feedId) {
        Set<Long> feedIds = Sets.newHashSet(feedId);
        List<Feed> feeds = list(feedIds);

        return feeds.isEmpty() ? Feed.NOT_EXIST_FEED : feeds.get(0);
    }

    private List<Feed> list(Collection<Long> feedIds) {
        if (feedIds.isEmpty()) return new ArrayList<Feed>();

        String sql = "SELECT Id,`Type`, UserId, Content, CourseId, CourseTitle, Lng, Lat, CommentCount, StarCount, AddTime FROM SG_Feed WHERE Id IN (" + StringUtils.join(feedIds, ",") + ") AND Status=1";
        List<Feed> feeds = queryList(sql, Feed.class);

        Map<Long, List<String>> imgs = queryImgs(feedIds);
        Map<Long, Feed> feedsMap = new HashMap<Long, Feed>();
        for (Feed feed : feeds) {
            feed.setImgs(imgs.get(feed.getId()));
            feedsMap.put(feed.getId(), feed);
        }

        List<Feed> result = new ArrayList<Feed>();
        for (long feedId : feedIds) {
            Feed feed = feedsMap.get(feedId);
            if (feed != null) result.add(feed);
        }

        return result;
    }

    private Map<Long, List<String>> queryImgs(Collection<Long> feedIds) {
        if (feedIds.isEmpty()) return new HashMap<Long, List<String>>();

        final Map<Long, List<String>> imgs = new HashMap<Long, List<String>>();
        for (long feedId : feedIds) {
            imgs.put(feedId, new ArrayList<String>());
        }

        String sql = "SELECT FeedId, Url FROM SG_FeedImg WHERE FeedId IN (" + StringUtils.join(feedIds, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long feedId = rs.getLong("FeedId");
                String url = rs.getString("Url");
                imgs.get(feedId).add(url);
            }
        });

        return imgs;
    }

    @Override
    public boolean delete(long userId, long feedId) {
        String sql = "UPDATE SG_Feed SET Status=0 WHERE Id=? AND UserId=?";
        if (update(sql, new Object[] { feedId, userId })) {
            sql = "UPDATE SG_FeedFollow SET Status=0 WHERE FeedId=?";
            return update(sql, new Object[] { feedId });
        }

        return false;
    }

    @Override
    public List<Long> getFollowedIds(long userId) {
        String sql = "SELECT FollowedId FROM SG_UserFollow WHERE UserId=? AND Status=1";
        return queryLongList(sql, new Object[] { userId });
    }

    @Override
    public long queryFollowedCountByUser(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_FeedFollow WHERE UserId=? AND Status=1";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<Feed> queryFollowedByUser(long userId, int start, int count) {
        String sql = "SELECT FeedId FROM SG_FeedFollow WHERE UserId=? AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        List<Long> feedIds = queryLongList(sql, new Object[] { userId, start, count });

        return list(feedIds);
    }

    @Override
    public long queryOfficialFeedsCount() {
        String sql = "SELECT COUNT(1) FROM SG_Feed WHERE Official=1 AND Status=1";
        return queryLong(sql, null);
    }

    @Override
    public List<Feed> queryOfficialFeeds(int start, int count) {
        String sql = "SELECT Id FROM SG_Feed WHERE Official=1 AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        List<Long> feedIds = queryLongList(sql, new Object[] { start, count });

        return list(feedIds);
    }

    @Override
    public void increaseCommentCount(long feedId) {
        String sql = "UPDATE SG_Feed SET CommentCount=CommentCount+1 WHERE Id=?";
        update(sql, new Object[] { feedId });
    }

    @Override
    public void decreaseCommentCount(long feedId) {
        String sql = "UPDATE SG_Feed SET CommentCount=CommentCount-1 WHERE Id=? AND CommentCount>=1";
        update(sql, new Object[] { feedId });
    }

    @Override
    public void increaseStarCount(long feedId) {
        String sql = "UPDATE SG_Feed SET StarCount=StarCount+1 WHERE Id=?";
        update(sql, new Object[] { feedId });
    }

    @Override
    public void decreaseStarCount(long feedId) {
        String sql = "UPDATE SG_Feed SET StarCount=StarCount-1 WHERE Id=? AND CommentCount>=1";
        update(sql, new Object[] { feedId });
    }
}
