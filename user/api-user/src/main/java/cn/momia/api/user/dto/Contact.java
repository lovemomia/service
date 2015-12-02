package cn.momia.api.user.dto;

import org.apache.commons.lang3.StringUtils;

public class Contact extends User {
    private User user;

    public Contact(User user) {
        this.user = user;
    }

    public String getName() {
        if (StringUtils.isBlank(user.getName())) return user.getNickName();
        return user.getName();
    }

    public String getMobile() {
        return user.getMobile();
    }
}
