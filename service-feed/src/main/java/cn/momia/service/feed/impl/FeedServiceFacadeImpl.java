package cn.momia.service.feed.impl;

import cn.momia.common.service.impl.DbAccessService;
import cn.momia.service.feed.Feed;
import cn.momia.service.feed.FeedImage;
import cn.momia.service.feed.FeedServiceFacade;
import cn.momia.service.feed.base.BaseFeed;
import cn.momia.service.feed.base.BaseFeedService;
import cn.momia.service.feed.comment.FeedComment;
import cn.momia.service.feed.comment.FeedCommentService;
import cn.momia.service.feed.star.FeedStarService;
import cn.momia.service.feed.topic.FeedTopic;
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
    public Feed get(long id) {
        if (id <= 0) return Feed.NOT_EXIST_FEED;

        BaseFeed baseFeed = baseFeedService.get(id);
        if (!baseFeed.exists()) return Feed.NOT_EXIST_FEED;

        return buildFeed(baseFeed);
    }

    private Feed buildFeed(BaseFeed baseFeed) {
        FeedTopic feedTopic = feedTopicService.get(baseFeed.getTopicId());
        if (!feedTopic.exists()) return Feed.NOT_EXIST_FEED;

        Feed feed = new Feed();
        feed.setBaseFeed(baseFeed);
        feed.setFeedTopic(feedTopic);
        feed.setImgs(getFeedImgs(baseFeed.getId()));
        feed.setCommentCount(feedCommentService.queryCount(baseFeed.getId()));
        feed.setStarCount(feedStarService.queryUserCount(baseFeed.getId()));

        return feed;
    }

    @Override
    public long queryCommentsCount(long id) {
        if (id <= 0) return 0;
        return feedCommentService.queryCount(id);
    }

    @Override
    public List<FeedComment> queryComments(long id, int start, int count) {
        if (id <= 0 || start < 0 || count <= 0) return new ArrayList<FeedComment>();

        return feedCommentService.query(id, start, count);
    }

    @Override
    public long queryStaredUsersCount(long id) {
        if (id <= 0) return 0;
        return feedStarService.queryUserCount(id);
    }

    @Override
    public List<Long> queryStaredUserIds(long id, int start, int count) {
        if (id <= 0 || start < 0 || count <= 0) return new ArrayList<Long>();

        return feedStarService.queryUserIds(id, start, count);
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
