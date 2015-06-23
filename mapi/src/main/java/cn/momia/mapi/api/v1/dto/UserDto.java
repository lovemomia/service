package cn.momia.mapi.api.v1.dto;

import java.util.Date;

public class UserDto {
    public static class Own extends UserDto {}

    public static class Other extends UserDto {
        public String getToken() {
            return "";
        }

        public String getMobile() {
            return mobile.substring(0, 3) + "******" + mobile.substring(9);
        }

        public String getAddress() {
            return "";
        }
    }

    protected String token;
    protected String mobile;
    protected String avatar;
    protected String name;
    protected String sex;
    protected Date birthday;
    protected int cityId;
    protected String address;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
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

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    protected UserDto() {}
}
