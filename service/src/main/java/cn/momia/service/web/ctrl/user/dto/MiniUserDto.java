package cn.momia.service.web.ctrl.user.dto;

import cn.momia.service.user.base.User;
import cn.momia.service.web.ctrl.dto.Dto;

public class MiniUserDto implements Dto {
    protected User user;

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
