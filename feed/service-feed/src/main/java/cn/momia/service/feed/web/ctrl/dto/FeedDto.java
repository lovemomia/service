package cn.momia.service.feed.web.ctrl.dto;

import cn.momia.api.user.dto.ParticipantDto;
import cn.momia.common.util.SexUtil;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.api.dto.Dto;
import cn.momia.service.feed.facade.Feed;
import cn.momia.service.feed.facade.FeedImage;
import cn.momia.api.user.dto.UserDto;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedDto implements Dto {
    private Feed feed;
    private UserDto user;
    private boolean stared;

    public long getId() {
        return feed.getId();
    }

    public int getType() {
        return feed.getType();
    }

    public long getTopicId() {
        return feed.getTopicId();
    }

    public long getTopicProductId() {
        return feed.getTopicProductId();
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

    public List<String> getChildren() {
        List<String> children = new ArrayList<String>();

        if (user.getChildren() != null) {
            int count = 0;
            for (ParticipantDto child : user.getChildren()) {
                if (TimeUtil.isAdult(child.getBirthday())) continue;

                String ageStr = TimeUtil.formatAge(child.getBirthday());
                if (SexUtil.isInvalid(child.getSex())) children.add("孩子" + ageStr);
                else children.add(child.getSex() + "孩" + ageStr);

                count++;
                if (count >= 2) break;
            }
        }

        return children;
    }

    public boolean isStared() {
        return stared;
    }

    public FeedDto(Feed feed, UserDto user) {
        this.feed = feed;
        this.user = user;
    }

    public FeedDto(Feed feed, UserDto user, boolean stared) {
        this(feed, user);
        this.stared = stared;
    }
}
