package cn.momia.api.feed.dto;

public class FeedTopicDto {
    public static class Type {
        public static final int PRODUCT = 1;
        public static final int COURSE = 2;
    }

    private long id;
    private int type;
    private long refId;

    private String title;
    private String scheduler;
    private String region;

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

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean exists() {
        return id > 0;
    }
}
