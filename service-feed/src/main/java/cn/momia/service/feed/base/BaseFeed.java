package cn.momia.service.feed.base;

import java.util.Date;

public class BaseFeed {
    public static final BaseFeed NOT_EXIST_BASE_FEED = new BaseFeed();
    public static final BaseFeed INVALID_BASE_FEED = new BaseFeed();
    static {
        NOT_EXIST_BASE_FEED.setId(0);
        INVALID_BASE_FEED.setId(0);
    }

    private long id;
    private int type;
    private long userId;
    private long productId;
    private long topicId;
    private String topic = "";
    private String content = "";
    private double lng;
    private double lat;
    private int commentCount;
    private int starCount;
    private Date addTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
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

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseFeed)) return false;

        BaseFeed baseFeed = (BaseFeed) o;

        return getId() == baseFeed.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_BASE_FEED);
    }

    public boolean isInvalid() {
        return type < 0 || userId <= 0;
    }
}
