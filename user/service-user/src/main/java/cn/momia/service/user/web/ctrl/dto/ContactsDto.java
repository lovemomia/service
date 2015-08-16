package cn.momia.service.user.web.ctrl.dto;

import cn.momia.service.user.base.User;

import java.io.Serializable;

public class ContactsDto implements Serializable {
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
