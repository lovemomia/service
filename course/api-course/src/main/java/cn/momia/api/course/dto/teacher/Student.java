package cn.momia.api.course.dto.teacher;

import cn.momia.common.core.util.TimeUtil;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class Student {
    public static class Type {
        public static final int CHILD = 1;
        public static final int PARENT = 2;
    }

    private int type = Type.CHILD;

    private long id;
    private long userId;
    private String avatar;
    private String name;
    @JSONField(format = "yyyy-MM-dd") private Date birthday;
    private String sex;

    private long packageId;
    private boolean checkin;
    private boolean commented;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public boolean isCheckin() {
        return checkin;
    }

    public void setCheckin(boolean checkin) {
        this.checkin = checkin;
    }

    public boolean isCommented() {
        return commented;
    }

    public void setCommented(boolean commented) {
        this.commented = commented;
    }

    public String getAge() {
        return birthday != null ? TimeUtil.formatAge(birthday) : "";
    }
}
