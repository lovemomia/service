package cn.momia.service.web.ctrl.user.dto;

import cn.momia.common.secret.MobileEncryptor;
import cn.momia.service.user.base.User;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class BaseUserDto extends MiniUserDto {
    public String getToken() {
        return user.getToken();
    }

    public String getMobile() {
        return MobileEncryptor.encrypt(user.getMobile());
    }

    public boolean isHasPassword() {
        return user.isHasPassword();
    }

    public String getName() {
        return user.getName();
    }

    public String getSex() {
        return user.getSex();
    }

    @JSONField(format = "yyyy-MM-dd")
    public Date getBirthday() {
        return user.getBirthday();
    }

    public int getCity() {
        return user.getCity();
    }

    public String getAddress() {
        return user.getAddress();
    }

    public BaseUserDto(User user) {
        super(user);
    }
}
