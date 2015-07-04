package cn.momia.service.base.user.participant;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

public class Participant implements Serializable {
    public static final Participant NOT_EXIST_PARTICIPANT = new Participant();
    static {
        NOT_EXIST_PARTICIPANT.setId(0);
    }

    private long id;
    private long userId;
    private String name;
    private String sex;
    private Date birthday;
    private int idType;
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
        return userId <= 0 || StringUtils.isBlank(name) || StringUtils.isBlank(sex) || birthday == null;
    }
}
