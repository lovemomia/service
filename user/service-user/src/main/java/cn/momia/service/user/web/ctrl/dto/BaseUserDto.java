package cn.momia.service.user.web.ctrl.dto;

import cn.momia.service.base.util.MobileUtil;
import cn.momia.service.user.base.User;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class BaseUserDto extends MiniUserDto {
    protected boolean showToken = true;

    public String getToken() {
        return showToken ? user.getToken() : null;
    }

    public String getMobile() {
        return MobileUtil.encrypt(user.getMobile());
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

    @Deprecated
    public int getCity() {
        return getCityId();
    }

    public int getCityId() {
        return user.getCityId();
    }

    public int getRegionId() {
        return user.getRegionId();
    }

    public String getAddress() {
        return user.getAddress();
    }

    public BaseUserDto(User user) {
        super(user);
    }

    public BaseUserDto(User user, boolean showToken) {
        super(user);
        this.showToken = showToken;
    }

}
