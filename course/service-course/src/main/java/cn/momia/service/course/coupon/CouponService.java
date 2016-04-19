package cn.momia.service.course.coupon;

import cn.momia.api.course.dto.coupon.Coupon;
import cn.momia.api.course.dto.coupon.CouponCode;
import cn.momia.api.course.dto.coupon.UserCoupon;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    Coupon getCoupon(int couponId);

    UserCoupon get(long userCouponId);
    BigDecimal calcTotalFee(BigDecimal totalFee, UserCoupon userCoupon);

    long queryCount(long userId, int status);
    List<UserCoupon> query(long userId, int status, int start, int count);

    UserCoupon queryByOrder(long orderId);
    UserCoupon queryUsedByOrder(long orderId);

    boolean preUseCoupon(long orderId, long userCouponId);
    boolean useCoupon(long orderId, long userCouponId);

    boolean hasInviteCoupon(String mobile);
    boolean addInviteCoupon(String mobile, String inviteCode);

    InviteCoupon getInviteCoupon(String mobile);
    boolean updateInviteCouponStatus(String mobile);
    UserCoupon distributeInviteUserCoupon(long userId, int couponId, String inviteCode);

    UserCoupon distributeFirstPayUserCoupon(long userId);

    List<UserCoupon> queryUserCouponsToExpired(int days);

    CouponCode getCouponCode(String code);

    boolean hasActivityCoupon(long userId);
    long distributeActivityCoupon(long userId, Coupon coupon);
}
