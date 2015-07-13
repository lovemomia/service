package cn.momia.service.promo.coupon;

import java.util.Date;

public class UserCoupon {
    public static class Type {
        public static final int NORMAL = 0;
        public static final int REGISTER = 1;
    }

    public static final UserCoupon NOT_EXIST_USER_COUPON = new UserCoupon();
    public static final UserCoupon INVALID_USER_COUPON = new UserCoupon();
    static {
        NOT_EXIST_USER_COUPON.setId(0);
        INVALID_USER_COUPON.setId(0);
    }

    private long id;
    private long userId;
    private int couponId;
    private int type;
    private Date expiredTime;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCoupon)) return false;

        UserCoupon that = (UserCoupon) o;

        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_USER_COUPON);
    }
}
