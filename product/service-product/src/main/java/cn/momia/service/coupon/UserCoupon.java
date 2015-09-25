package cn.momia.service.coupon;

import java.util.Date;

public class UserCoupon {
    public static class Status {
        public static final int NOT_USED = 1;
        public static final int USED = 2;
        public static final int EXPIRED = 3;
        public static final int LOCKED = 4;
    }

    public static final UserCoupon NOT_EXIST_USER_COUPON = new UserCoupon();

    private long id;
    private long userId;
    private int couponId;
    private Date startTime;
    private Date endTime;
    private long orderId;
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

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
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

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
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

    public boolean isExpired() {
        if (startTime == null || endTime == null) return true;
        return endTime.before(new Date());
    }
}
