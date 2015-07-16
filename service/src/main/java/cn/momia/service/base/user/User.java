package cn.momia.service.base.user;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class User implements Serializable {
    public static final User NOT_EXIST_USER = new User();
    public static final User INVALID_USER = new User();
    public static final User DUPLICATE_USER = new User() {
        public boolean duplicated() {
            return true;
        }
    };

    static {
        NOT_EXIST_USER.setId(0);
        INVALID_USER.setId(0);
        DUPLICATE_USER.setId(0);
    }

    private long id;
    private String token;
    private String nickName;
    private String mobile;
    private boolean hasPassword;
    private String avatar;
    private String name;
    private String sex;
    private Date birthday;
    private int city;
    private String address;
    private Set<Long> children;

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

    public Set<Long> getChildren() {
        return children;
    }

    public void setChildren(Set<Long> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return getId() == user.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_USER);
    }

    public boolean duplicated() {
        return false;
    }
}
