package cn.momia.api.user.dto;

import org.apache.commons.lang3.StringUtils;

public class Contact {
    private String name;
    private String mobile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Contact() {}

    public Contact(User user) {
        this.name = StringUtils.isBlank(user.getName()) ? user.getNickName() : user.getName();
        this.mobile = user.getMobile();
    }
}
