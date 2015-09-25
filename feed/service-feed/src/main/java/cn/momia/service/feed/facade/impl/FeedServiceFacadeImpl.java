package cn.momia.service.feed.facade.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.feed.facade.Feed;
import cn.momia.service.feed.facade.FeedImage;
import cn.momia.service.feed.facade.FeedServiceFacade;
import cn.momia.service.feed.base.BaseFeed;
import cn.momia.service.feed.base.BaseFeedService;
import cn.momia.service.feed.comment.FeedComment;
import cn.momia.service.feed.comment.FeedCommentService;
import cn.momia.service.feed.star.FeedStarService;
import cn.momia.service.feed.topic.FeedTopic;
import cn.momia.service.feed.topic.FeedTopicService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeedServiceFacadeImpl extends DbAccessService implements FeedServiceFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedServiceFacadeImpl.class);

    private BaseFeedService baseFeedService;
    private FeedTopicService feedTopicService;
    private FeedCommentService feedCommentService;
    private FeedStarService feedStarService;

    public void setBaseFeedService(BaseFeedService baseFeedService) {
        this.baseFeedService = baseFeedService;
    }

    public void setFeedTopicService(FeedTopicService feedTopicService) {
        this.feedTopicService = feedTopicService;
    }

    public void setFeedCommentService(FeedCommentService feedCommentService) {
        this.feedCommentService = feedCommentService;
    }

    public void setFeedStarService(FeedStarService feedStarService) {
        this.feedStarService = feedStarService;
    }

    @Override
    public boolean follow(long ownUserId, long otherUserId) {
        if (ownUserId <= 0 || otherUserId <= 0) return false;
        if (baseFeedService.isFollowed(ownUserId, otherUserId)) return true;
        return baseFeedService.follow(ownUserId, otherUserId);
    }

    @Override
    public long addFeed(Feed feed) {
        if (feed.isInvalid()) return 0;
        long feedId = baseFeedService.add(feed.getBaseFeed());
        if (feedId <= 0) return 0;

        addFeedImgs(feedId, feed.getImgs());

        return feedId;
    }

    private void addFeedImgs(long feedId, List<FeedImage> imgs) {
        try {
            String sql = "INSERT INTO t_feed_img(feedId, url, width, height, addTime) VALUES (?, ?, ?, ?, NOW())";
            List<Object[]> args = new ArrayList<Object[]>();
            for (FeedImage img : imgs) {
                args.add(new Object[] { feedId, img.getUrl(), img.getWidth(), img.getHeight() });
            }
            jdbcTemplate.batchUpdate(sql, args);
        } catch (Exception e) {
            LOGGER.error("fail to add image for feed: {}", feedId, e);
        }
    }

    @Override
    public void pushFeed(long feedId, Collection<Long> followedIds) {
        if (feedId <= 0 || followedIds == null || followedIds.isEmpty()) return;
        String sql = "INSERT INTO t_feed_follow(userId, feedId, addTime) VALUES (?, ?, NOW())";
        List<Object[]> args = new ArrayList<Object[]>();
        for (long followedId : followedIds) {
            args.add(new Object[] { followedId, feedId });
        }
        jdbcTemplate.batchUpdate(sql, args);
    }

    @Override
    public Feed getFeed(long feedId) {
        if (feedId <= 0) return Feed.NOT_EXIST_FEED;

        BaseFeed baseFeed = baseFeedService.get(feedId);
        if (!baseFeed.exists()) return Feed.NOT_EXIST_FEED;

        return buildFeed(baseFeed);
    }

    private Feed buildFeed(BaseFeed baseFeed) {
        Feed feed = new Feed();
        feed.setBaseFeed(baseFeed);
        feed.setImgs(getFeedImgs(baseFeed.getId()));

        return feed;
    }

    private List<FeedImage> getFeedImgs(long feedId) {
        final List<FeedImage> imgs = new ArrayList<FeedImage>();
        String sql = "SELECT url, width, height FROM t_feed_img WHERE feedId=? AND status=1";
        jdbcTemplate.query(sql, new Object[] { feedId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                imgs.add(buildImage(rs));
            }
        });

        return imgs;
    }

    private FeedImage buildImage(ResultSet rs) throws SQLException {
        FeedImage img = new FeedImage();
        img.setUrl(rs.getString("url"));
        img.setWidth(rs.getInt("width"));
        img.setHeight(rs.getInt("height"));

        return img;
    }

    @Override
    public boolean deleteFeed(long userId, long feedId) {
        if (userId <= 0 || feedId <= 0) return false;
        return baseFeedService.delete(userId, feedId);
    }

    @Override
    public List<Long> queryFollowedIds(long userId) {
        if (userId <= 0) return new ArrayList<Long>();
        return baseFeedService.getFollowedIds(userId);
    }

    @Override
    public long queryFollowedCountByUser(long userId) {
        if (userId <= 0) return baseFeedService.queryOfficialFeedsCount();
        return baseFeedService.queryFollowedCountByUser(userId);
    }

    @Override
    public List<Feed> queryFollowedByUser(long userId, int start, int count) {
        if (start < 0 || count <= 0) return new ArrayList<Feed>();
        List<BaseFeed> baseFeeds = userId <= 0 ? baseFeedService.queryOfficialFeeds(start, count) : baseFeedService.queryFollowedByUser(userId, start, count);
        List<Feed> feeds = new ArrayList<Feed>();
        for (BaseFeed baseFeed : baseFeeds) {
            feeds.add(buildFeed(baseFeed));
        }

        return feeds;
    }

    @Override
    public long queryCommentsCount(long feedId) {
        if (feedId <= 0) return 0;
        return feedCommentService.queryCount(feedId);
    }

    @Override
    public List<FeedComment> queryComments(long feedId, int start, int count) {
        if (feedId <= 0 || start < 0 || count <= 0) return new ArrayList<FeedComment>();
        return feedCommentService.query(feedId, start, count);
    }

    @Override
    public long queryStaredUsersCount(long feedId) {
        if (feedId <= 0) return 0;
        return feedStarService.queryUserCount(feedId);
    }

    @Override
    public List<Long> queryStaredUserIds(long feedId, int start, int count) {
        if (feedId <= 0 || start < 0 || count <= 0) return new ArrayList<Long>();
        return feedStarService.queryUserIds(feedId, start, count);
    }

    @Override
    public FeedTopic getTopic(long topicId) {
        return feedTopicService.get(topicId);
    }

    @Override
    public List<FeedTopic> list(Collection<Long> topicIds) {
        if (topicIds == null || topicIds.isEmpty()) return new ArrayList<FeedTopic>();
        return feedTopicService.list(topicIds);
    }

    @Override
    public long queryCountByTopic(long topicId) {
        if (topicId <= 0) return 0;
        return baseFeedService.queryCountByTopic(topicId);
    }

    @Override
    public List<Feed> queryByTopic(long topicId, int start, int count) {
        if (topicId <= 0 || start < 0 || count <= 0) return new ArrayList<Feed>();
        List<BaseFeed> baseFeeds = baseFeedService.queryByTopic(topicId, start, count);

        List<Feed> feeds = new ArrayList<Feed>();
        for (BaseFeed baseFeed : baseFeeds) {
            Feed feed = buildFeed(baseFeed);
            if (feed.exists()) feeds.add(feed);
        }
        
        return feeds;
    }

    @Override
    public long queryTopicCount(int type) {
        return feedTopicService.queryCount(type);
    }

    @Override
    public List<FeedTopic> queryTopic(int type, int start, int count) {
        if (start < 0 || count <= 0) return new ArrayList<FeedTopic>();
        return feedTopicService.query(type, start, count);
    }

    @Override
    public boolean addComment(long userId, long feedId, String content) {
        if (userId <= 0 || feedId <= 0 || StringUtils.isBlank(content)) return false;
        return feedCommentService.add(userId, feedId, content);
    }

    @Override
    public boolean deleteComment(long userId, long feedId, long commentId) {
        if (userId <= 0 || feedId <= 0 || commentId <= 0) return false;
        return feedCommentService.delete(userId, feedId, commentId);
    }

    @Override
    public void increaseCommentCount(long feedId) {
        if (feedId <= 0) return;
        baseFeedService.increaseCommentCount(feedId);
    }

    @Override
    public void decreaseCommentCount(long feedId) {
        if (feedId <= 0) return;
        baseFeedService.decreaseCommentCount(feedId);
    }

    @Override
    public boolean isStared(long userId, long feedId) {
        if (userId <= 0 || feedId <= 0) return false;
        return feedStarService.isStared(userId, feedId);
    }

    @Override
    public List<Long> queryStaredFeeds(long userId, Collection<Long> feedIds) {
        if (userId <= 0 || feedIds == null || feedIds.isEmpty()) return new ArrayList<Long>();
        return feedStarService.queryStaredFeeds(userId, feedIds);
    }

    @Override
    public boolean star(long userId, long feedId) {
        if (userId <= 0 || feedId <= 0) return false;
        return feedStarService.add(userId, feedId);
    }

    @Override
    public boolean unstar(long userId, long feedId) {
        if (userId <= 0 || feedId <= 0) return false;
        return feedStarService.delete(userId, feedId);
    }

    @Override
    public void increaseStarCount(long feedId) {
        if (feedId <= 0) return;
        baseFeedService.increaseStarCount(feedId);
    }

    @Override
    public void decreaseStarCount(long feedId) {
        if (feedId <= 0) return;
        baseFeedService.decreaseStarCount(feedId);
    }
}
