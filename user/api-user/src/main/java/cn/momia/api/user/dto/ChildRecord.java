package cn.momia.api.user.dto;

import java.util.List;

public class ChildRecord {
    private List<Integer> tags;
    private String content;

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
