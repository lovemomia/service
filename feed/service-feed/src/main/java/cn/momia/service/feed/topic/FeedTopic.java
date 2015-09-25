package cn.momia.service.feed.topic;

public class FeedTopic {
    public static class Type {
        public static final int PRODUCT = 1;
        public static final int COURSE = 2;
    }

    public static final FeedTopic NOT_EXIST_FEED_TOPIC = new FeedTopic();

    private long id;
    private int type;
    private long refId;
    private String title;

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

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean exists() {
        return id > 0;
    }
}
