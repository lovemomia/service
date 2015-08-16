package cn.momia.service.user.web.ctrl.dto;

import cn.momia.service.user.base.User;

import java.io.Serializable;

public class MiniUserDto implements Serializable {
    protected User user;

    public long getId() {
        return user.getId();
    }

    public String getNickName() {
        return user.getNickName();
    }

    public String getAvatar() {
        return user.getAvatar();
    }

    public MiniUserDto(User user) {
        this.user = user;
    }
}
