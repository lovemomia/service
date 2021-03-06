package cn.momia.api.course.dto.coupon;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class UserCoupon {
    public static class Status {
        public static final int NOT_USED = 1;
        public static final int USED = 2;
        public static final int EXPIRED = 3;
    }

    public static final UserCoupon NOT_EXIST_USER_COUPON = new UserCoupon();

    private long id;
    private int type;
    private long userId;
    @JSONField(serialize = false) private int couponId;
    private String title;
    private String desc;
    private BigDecimal discount;
    private BigDecimal consumption;
    @JSONField(format = "yyyy-MM-dd") private Date startTime;
    @JSONField(format = "yyyy-MM-dd") private Date endTime;
    @JSONField(serialize = false) private String inviteCode;
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getConsumption() {
        return consumption;
    }

    public void setConsumption(BigDecimal consumption) {
        this.consumption = consumption;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
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

    @JSONField(serialize = false)
    public boolean isUsed() {
        return status == Status.USED;
    }

    @JSONField(serialize = false)
    public boolean isExpired() {
        return (status == Status.NOT_USED && endTime.before(new Date())) || status == Status.EXPIRED;
    }
}
