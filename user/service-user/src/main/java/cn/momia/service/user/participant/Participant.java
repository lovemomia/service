package cn.momia.service.user.participant;

import cn.momia.common.util.SexUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

public class Participant implements Serializable {
    public static final Participant NOT_EXIST_PARTICIPANT = new Participant();

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

    public boolean exists() {
        return id > 0;
    }

    public boolean isInvalid() {
        return userId <= 0 || StringUtils.isBlank(name) || SexUtil.isInvalid(sex) || birthday == null;
    }
}
