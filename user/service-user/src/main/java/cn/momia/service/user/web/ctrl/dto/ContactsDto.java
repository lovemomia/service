package cn.momia.service.user.web.ctrl.dto;

import cn.momia.common.webapp.ctrl.dto.Dto;
import cn.momia.service.user.base.User;
import org.apache.commons.lang3.StringUtils;

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
        if (StringUtils.isBlank(this.name)) this.name = user.getNickName();

        this.mobile = user.getMobile();
    }
}
