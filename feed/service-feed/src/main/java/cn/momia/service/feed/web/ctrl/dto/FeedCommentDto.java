package cn.momia.service.feed.web.ctrl.dto;

import cn.momia.service.feed.comment.FeedComment;
import cn.momia.service.user.api.user.User;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class FeedCommentDto {
    private FeedComment comment;
    private User user;

    public long getId() {
        return comment.getId();
    }

    public String getContent() {
        return comment.getContent();
    }

    @JSONField(format = "yyyy-MM-dd HH:mm")
    public Date getAddTime() {
        return comment.getAddTime();
    }

    public String getAvatar() {
        return user.getAvatar();
    }

    public String getNickName() {
        return user.getNickName();
    }

    public FeedCommentDto(FeedComment comment, User user) {
        this.comment = comment;
        this.user = user;
    }
}
