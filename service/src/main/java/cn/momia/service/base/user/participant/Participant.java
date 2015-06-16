package cn.momia.service.base.user.participant;

import java.util.Date;

public class Participant {
    public static final Participant NOT_EXIST_PARTICIPANT = new Participant();
    static {
        NOT_EXIST_PARTICIPANT.setId(0);
    }

    private long id;
    private long userId;
    private String name;
    private int sex;
    private Date birthday;

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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_PARTICIPANT);
    }
}
