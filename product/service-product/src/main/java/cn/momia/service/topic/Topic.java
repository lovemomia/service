package cn.momia.service.topic;

public class Topic {
    public static final Topic NOT_EXIST_TOPIC = new Topic();

    private long id;
    private String cover;
    private String title;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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
