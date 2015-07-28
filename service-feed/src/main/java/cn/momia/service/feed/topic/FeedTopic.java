package cn.momia.service.feed.topic;

public class FeedTopic {
    public static final FeedTopic NOT_EXIST_FEED_TOPIC = new FeedTopic();
    public static final FeedTopic INVALID_FEED_TOPIC = new FeedTopic();
    static {
        NOT_EXIST_FEED_TOPIC.setId(0);
        INVALID_FEED_TOPIC.setId(0);
    }

    private long id;
    private String title;
    private long productId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeedTopic)) return false;

        FeedTopic feedTopic = (FeedTopic) o;

        return getId() == feedTopic.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_FEED_TOPIC);
    }
}
