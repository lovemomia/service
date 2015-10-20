package cn.momia.api.user.dto;

import cn.momia.common.util.MobileUtil;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class UserDto {
    public static class Type {
        public static final int MINI = 1;
        public static final int BASE = 2;
        public static final int FULL = 3;
    }

    private long id;
    private String nickName;
    private String avatar;

    private String mobile;
    private String name;
    private String sex;
    private Date birthday;
    private Integer cityId;
    private Integer regionId;
    private String address;

    private String token;

    private List<ChildDto> children;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMobile() {
        return MobileUtil.encrypt(mobile);
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @JSONField(format = "yyyy-MM-dd")
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<ChildDto> getChildren() {
        return children;
    }

    public void setChildren(List<ChildDto> children) {
        this.children = children;
    }

    public boolean exists() {
        return id > 0;
    }
}
