package cn.momia.service.feed.web.ctrl.dto;

import cn.momia.common.api.dto.Dto;
import cn.momia.service.feed.comment.FeedComment;
import cn.momia.api.user.dto.UserDto;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class FeedCommentDto implements Dto {
    private FeedComment comment;
    private UserDto user;

    public long getId() {
        return comment.getId();
    }

    public String getContent() {
        return comment.getContent();
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getAddTime() {
        return comment.getAddTime();
    }

    public String getAvatar() {
        return user.getAvatar();
    }

    public String getNickName() {
        return user.getNickName();
    }

    public FeedCommentDto(FeedComment comment, UserDto user) {
        this.comment = comment;
        this.user = user;
    }
}
