package cn.momia.service.discuss;

import java.util.Date;

public class DiscussReply {
    private long id;
    private int topicId;
    private long userId;
    private String content;
    private Date addTime;

    private long staredCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public long getStaredCount() {
        return staredCount;
    }

    public void setStaredCount(long staredCount) {
        this.staredCount = staredCount;
    }
}
