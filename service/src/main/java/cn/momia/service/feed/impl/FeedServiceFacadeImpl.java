package cn.momia.service.feed.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.feed.Feed;
import cn.momia.service.feed.FeedImage;
import cn.momia.service.feed.FeedServiceFacade;
import cn.momia.service.feed.base.BaseFeed;
import cn.momia.service.feed.base.BaseFeedService;
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

        FeedTopic feedTopic = feedTopicService.get(baseFeed.getTopicId());
        if (!feedTopic.exists()) return Feed.NOT_EXIST_FEED;

        Feed feed = new Feed();
        feed.setBaseFeed(baseFeed);
        feed.setFeedTopic(feedTopic);
        feed.setImgs(getFeedImgs(id));
        feed.setCommentCount(feedCommentService.getCount(baseFeed.getId()));
        feed.setStarCount(feedStarService.getCount(baseFeed.getId()));

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
}
