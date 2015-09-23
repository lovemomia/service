package cn.momia.service.feed.topic;

public class FeedTopic {
    public static final FeedTopic NOT_EXIST_FEED_TOPIC = new FeedTopic();

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

    public boolean exists() {
        return id > 0;
    }
}
