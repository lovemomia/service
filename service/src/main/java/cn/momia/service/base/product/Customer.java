package cn.momia.service.base.product;

import cn.momia.service.base.user.participant.Participant;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Customer implements Serializable {
    private long userId;
    private String avatar;
    private String name;
    private List<Participant> participants;
    private Date orderDate;
    private int orderStatus;
    private String nickName;
    private Set<Participant> children;


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

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Set<Participant> getChildren() {
        return children;
    }

    public void setChildren(Set<Participant> children) {
        this.children = children;
    }
}
