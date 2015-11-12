package cn.momia.service.course.subject.coupon;

public class InviteCoupon {
    public static final InviteCoupon NOT_EXIST_INVITE_COUPON = new InviteCoupon();

    private long id;
    private String mobile;
    private String inviteCode;
    private int couponId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public boolean exists() {
        return id > 0;
    }
}
