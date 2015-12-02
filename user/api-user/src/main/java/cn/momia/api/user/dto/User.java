package cn.momia.api.user.dto;

import cn.momia.common.util.MobileUtil;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class User {
    public static final User NOT_EXIST_USER = new User();

    public static class Type {
        public static final int MINI = 1;
        public static final int BASE = 2;
        public static final int FULL = 3;
    }

    private long id;
    private String nickName;
    private String avatar;

    private String mobile;
    private String cover;
    private String name;
    private String sex;
    private Date birthday;
    private int cityId;
    private int regionId;
    private String address;
    private int payed;
    private String inviteCode;

    private String token;

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

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPayed() {
        return payed;
    }

    public void setPayed(int payed) {
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

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public boolean exists() {
        return id > 0;
    }

    public static class Mini {
        protected User user;

        public Mini(User user) {
            this.user = user;
        }

        public long getId() {
            return user.getId();
        }

        public String getNickName() {
            return user.getNickName();
        }

        public String getAvatar() {
            return user.getAvatar();
        }
    }

    public static class Base extends Mini {
        private boolean showToken = true;

        public Base(User user) {
            super(user);
        }

        public Base(User user, boolean showToken) {
            this(user);
            this.showToken = showToken;
        }
        public String getMobile() {
            return MobileUtil.encrypt(user.getMobile());
        }

        public String getCover() {
            return user.getCover();
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

        public int getCityId() {
            return user.getCityId();
        }

        public int getRegionId() {
            return user.getRegionId();
        }

        public String getAddress() {
            return user.getAddress();
        }

        public boolean isPayed() {
            return user.getPayed() == 1;
        }

        public String getInviteCode() {
            return user.getInviteCode();
        }

        public String getToken() {
            return showToken ? user.getToken() : "";
        }
    }

    public static class Full extends Base {
        public Full(User user) {
            super(user);
        }

        public Full(User user, boolean showToken) {
            super(user, showToken);
        }

        public List<Child> getChildren() {
            return user.getChildren();
        }
    }
}
