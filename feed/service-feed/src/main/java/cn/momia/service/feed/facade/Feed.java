package cn.momia.service.feed.facade;

import cn.momia.service.feed.base.BaseFeed;

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
    private List<FeedImage> imgs;

    public BaseFeed getBaseFeed() {
        return baseFeed;
    }

    public void setBaseFeed(BaseFeed baseFeed) {
        this.baseFeed = baseFeed;
    }

    public List<FeedImage> getImgs() {
        return imgs;
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
        return baseFeed.getTopicId();
    }

    public long getTopicProductId() {
        return baseFeed.getProductId();
    }

    public String getTopic() {
        return baseFeed.getTopic();
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
        return baseFeed.getCommentCount();
    }

    public int getStarCount() {
        return baseFeed.getStarCount();
    }

    public boolean exists() {
        return getId() > 0;
    }

    public boolean isInvalid() {
        return baseFeed == null || baseFeed.isInvalid();
    }
}
