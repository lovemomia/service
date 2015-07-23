package cn.momia.service.web.ctrl.user.dto;

import cn.momia.service.user.base.User;
import cn.momia.service.web.ctrl.dto.Dto;

public class ContactsDto implements Dto {
    private String name;
    private String mobile;

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public ContactsDto(User user) {
        this.name = user.getName();
        this.mobile = user.getMobile();
    }
}
