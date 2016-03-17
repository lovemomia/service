package cn.momia.service.feed.base;

public class FeedTag {
    public static final FeedTag NOT_EXISTS_FEED_TAG = new FeedTag();

    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean exists() {
        return id > 0;
    }
}
