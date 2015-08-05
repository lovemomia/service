package cn.momia.service.feed.facade.impl;

import cn.momia.common.service.impl.DbAccessService;
import cn.momia.service.feed.facade.Feed;
import cn.momia.service.feed.facade.FeedImage;
import cn.momia.service.feed.facade.FeedServiceFacade;
import cn.momia.service.feed.base.BaseFeed;
import cn.momia.service.feed.base.BaseFeedService;
import cn.momia.service.feed.comment.FeedComment;
import cn.momia.service.feed.comment.FeedCommentService;
import cn.momia.service.feed.star.FeedStarService;
import cn.momia.service.feed.topic.FeedTopicService;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeedServiceFacadeImpl extends DbAccessService implements FeedServiceFacade {
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
    public Feed get(long feedId) {
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
    public long queryFollowedCountByUser(long userId) {
        if (userId <= 0) return 0;
        return baseFeedService.queryFollowedCountByUser(userId);
    }

    @Override
    public List<Feed> queryFollowedByUser(long userId, int start, int count) {
        if (userId <= 0 || start < 0 || count <= 0) return new ArrayList<Feed>();
        List<BaseFeed> baseFeeds = baseFeedService.queryFollowedByUser(userId, start, count);
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
}
