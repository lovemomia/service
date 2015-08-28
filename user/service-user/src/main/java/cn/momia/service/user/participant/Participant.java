package cn.momia.service.user.participant;

import cn.momia.service.base.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Participant implements Serializable {
    private static final Set<String> SEX = new HashSet<String>();
    static {
        SEX.add("男");
        SEX.add("女");
    }

    public static final Participant NOT_EXIST_PARTICIPANT = new Participant();
    public static final Participant INVALID_PARTICIPANT = new Participant();
    static {
        NOT_EXIST_PARTICIPANT.setId(0);
        INVALID_PARTICIPANT.setId(0);
    }

    private long id;
    private long userId;
    private String name;
    private String sex;
    private Date birthday;
    private int idType = 1;
    private String idNo;

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

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant)) return false;

        Participant that = (Participant) o;

        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_PARTICIPANT);
    }

    public boolean isInvalid() {
        return userId <= 0 || StringUtils.isBlank(name) || !SEX.contains(sex) || birthday == null;
    }

    public boolean isAdult() {
        return TimeUtil.isAdult(this.birthday);
    }

    public boolean isChild() {
        return TimeUtil.isChild(this.birthday);
    }

    public String getDesc() {
        if (TimeUtil.isAdult(this.birthday)) return "成人";

        String ageStr = TimeUtil.getAgeDesc(birthday);
        if (!("男".equals(this.sex) || "女".equals(this.sex))) return "孩子" + ageStr;
        return this.sex + "孩" + ageStr;
    }
}
