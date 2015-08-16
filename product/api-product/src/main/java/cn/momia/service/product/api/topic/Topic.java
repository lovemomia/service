package cn.momia.service.product.api.topic;

import java.util.List;

public class Topic {
    private long id;
    private String cover;
    private String title;
    private List<TopicGroup> groups;

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

    public List<TopicGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<TopicGroup> groups) {
        this.groups = groups;
    }
}
