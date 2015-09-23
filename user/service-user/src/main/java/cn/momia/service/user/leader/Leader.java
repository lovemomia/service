package cn.momia.service.user.leader;

import cn.momia.common.util.MobileUtil;
import org.apache.commons.lang3.StringUtils;

public class Leader {
    public static class Status {
        public static final int NOTEXIST = 0;
        public static final int PASSED = 1;
        public static final int AUDITING = 2;
        public static final int REJECTED = 3;
    }

    public static final Leader NOT_EXIST_LEADER = new Leader();

    private long id;
    private long userId;
    private String name;
    private String mobile;
    private int cityId;
    private int regionId;
    private String address;
    private String career;
    private String intro;
    private String msg;
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean exists() {
        return id > 0;
    }

    public boolean isInvalid() {
        return userId <= 0 ||
                StringUtils.isBlank(name) ||
                MobileUtil.isInvalid(mobile) ||
//                cityId <= 0 ||
//                regionId <= 0 ||
//                StringUtils.isBlank(address) ||
                StringUtils.isBlank(career) ||
                StringUtils.isBlank(intro);
    }
}
