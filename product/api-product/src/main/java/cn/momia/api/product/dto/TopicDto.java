package cn.momia.api.product.dto;

import java.util.List;

public class TopicDto {
    private long id;
    private String cover;
    private String title;
    private List<TopicGroupDto> groups;

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

    public List<TopicGroupDto> getGroups() {
        return groups;
    }

    public void setGroups(List<TopicGroupDto> groups) {
        this.groups = groups;
    }

    public boolean exists() {
        return id > 0;
    }
}
