package cn.momia.service.user.web.ctrl.dto;

import cn.momia.common.webapp.ctrl.dto.Dto;
import cn.momia.service.user.base.User;

public class MiniUserDto implements Dto {
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
