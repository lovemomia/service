package cn.momia.service.web.ctrl.feed.dto;

import cn.momia.service.feed.Feed;
import cn.momia.service.feed.FeedImage;
import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.web.ctrl.user.dto.ParticipantDto;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedDto {
    private Feed feed;
    private User user;
    private List<Participant> children;

    public long getId() {
        return feed.getId();
    }

    public int getType() {
        return feed.getType();
    }

    public long getTopicId() {
        return feed.getTopicId();
    }

    public long getTpoicProductId() {
        return feed.getTpoicProductId();
    }

    public String getTopic() {
        return feed.getTopic();
    }

    public List<String> getImgs() {
        List<String> imgs = new ArrayList<String>();
        for (FeedImage feedImage : feed.getImgs()) imgs.add(feedImage.getUrl());

        return imgs;
    }

    public String getContent() {
        return feed.getContent();
    }

    @JSONField(format = "yyyy-MM-dd")
    public Date getAddTime() {
        return feed.getAddTime();
    }

    public String getPoi() {
        return feed.getPoi();
    }

    public int getCommentCount() {
        return feed.getCommentCount();
    }

    public int getStarCount() {
        return feed.getStarCount();
    }

    public long getUserId() {
        return user.getId();
    }

    public String getAvatar() {
        return user.getAvatar();
    }

    public String getNickName() {
        return user.getNickName();
    }

    public List<ParticipantDto> getChildren() {
        List<ParticipantDto> childrenDto = new ArrayList<ParticipantDto>();
        for (Participant child : children) {
            childrenDto.add(new ParticipantDto(child));
        }

        return childrenDto;
    }

    public FeedDto(Feed feed, User user, List<Participant> children) {
        this.feed = feed;
        this.user = user;
        this.children = children;
    }
}
