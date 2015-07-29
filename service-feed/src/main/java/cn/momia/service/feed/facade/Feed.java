package cn.momia.service.feed.facade;

import cn.momia.service.feed.base.BaseFeed;
import cn.momia.service.feed.topic.FeedTopic;

import java.util.Date;
import java.util.List;

public class Feed {
    public static final Feed NOT_EXIST_FEED = new Feed() {
        @Override
        public long getId() {
            return 0;
        }
    };

    private BaseFeed baseFeed;
    private FeedTopic feedTopic;
    private List<FeedImage> imgs;
    private int commentCount;
    private int starCount;

    public void setBaseFeed(BaseFeed baseFeed) {
        this.baseFeed = baseFeed;
    }

    public void setFeedTopic(FeedTopic feedTopic) {
        this.feedTopic = feedTopic;
    }

    public void setImgs(List<FeedImage> imgs) {
        this.imgs = imgs;
    }

    public long getId() {
        return baseFeed.getId();
    }

    public int getType() {
        return baseFeed.getType();
    }

    public long getUserId() {
        return baseFeed.getUserId();
    }

    public long getTopicId() {
        return feedTopic.getId();
    }

    public long getTpoicProductId() {
        return feedTopic.getProductId();
    }

    public String getTopic() {
        return feedTopic.getTitle();
    }

    public List<FeedImage> getImgs() {
        return imgs;
    }

    public String getContent() {
        return baseFeed.getContent();
    }

    public String getPoi() {
        return baseFeed.getLng() + ":" + baseFeed.getLat();
    }

    public Date getAddTime() {
        return baseFeed.getAddTime();
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feed)) return false;

        Feed feed = (Feed) o;

        return getId() == feed.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_FEED);
    }
}
