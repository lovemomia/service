package cn.momia.mapi.api.v1.dto.user;

import cn.momia.common.web.img.ImageFile;
import cn.momia.mapi.api.v1.dto.base.Dto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class UserDto implements Dto {
    private long id;
    private String token;
    private String nickName;
    private String mobile;
    private boolean hasPassword;
    private String avatar;
    private String name;
    private String sex;
    @JSONField(format = "yyyy-MM-dd") private Date birthday;
    private int city;
    private String address;
    private JSONArray children;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    public String getAvatar() {
        return ImageFile.url(avatar);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public JSONArray getChildren() {
        return children;
    }

    public void setChildren(JSONArray children) {
        this.children = children;
    }
}
