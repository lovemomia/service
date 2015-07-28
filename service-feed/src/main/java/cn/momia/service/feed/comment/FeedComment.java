package cn.momia.service.feed.comment;

import java.util.Date;

public class FeedComment {
    public static final FeedComment NOT_EXIST_FEED_COMMENT = new FeedComment();
    public static final FeedComment INVALID_FEED_COMMENT = new FeedComment();
    static {
        NOT_EXIST_FEED_COMMENT.setId(0);
        INVALID_FEED_COMMENT.setId(0);
    }

    private long id;
    private long feedId;
    private long userId;
    private String content;
    private Date addTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFeedId() {
        return feedId;
    }

    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeedComment)) return false;

        FeedComment comment = (FeedComment) o;

        return getId() == comment.getId();

    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_FEED_COMMENT);
    }
}
