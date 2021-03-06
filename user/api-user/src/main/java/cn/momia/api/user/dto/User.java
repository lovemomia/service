package cn.momia.api.user.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class User {
    public static class Type {
        public static final int MINI = 1;
        public static final int BASE = 2;
        public static final int FULL = 3;
    }

    public static class Role {
        public static final int NORMAL = 1;
        public static final int TEACHER = 2;
        public static final int ADMIN = 3;
    }

    private long id;
    private String nickName;
    private String avatar;

    private String mobile;
    private String cover;
    private String name;
    private String sex;
    private Date birthday;
    private Integer cityId;
    private Integer regionId;
    private String address;
    private Boolean payed;
    private String inviteCode;

    private String token;
    private String imToken;
    private Integer role;

    private List<Child> children;

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
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public Boolean isPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImToken() {
        return imToken;
    }

    public void setImToken(String imToken) {
        this.imToken = imToken;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public boolean exists() {
        return id > 0;
    }

    @JSONField(serialize = false)
    public boolean isNormal() {
        return role != null && role == Role.NORMAL;
    }

    @JSONField(serialize = false)
    public boolean isTeacher() {
        return role != null && role == Role.TEACHER;
    }

    @JSONField(serialize = false)
    public boolean isAdmin() {
        return role != null && role == Role.ADMIN;
    }

    public boolean hasChild(long childId) {
        if (children == null || children.isEmpty()) return false;

        for (Child child : children) {
            if (child.getId() == childId) return true;
        }

        return false;
    }
}
