package cn.momia.service.promo.coupon;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CouponService {
    Coupon getCoupon(int couponId);
    Map<Integer, Coupon> getCoupons(Collection<Integer> couponIds);
    long getUserRegisterCoupon(long userId);
    int queryCountByUser(long userId, long orderId, int status);
    List<UserCoupon> queryByUser(long userId, long orderId, int status, int start, int count);
    boolean lockUserCoupon(long userId, long orderId, long userCouponId);
    boolean useUserCoupon(long userId, long orderId, long userCouponId);
    boolean releaseUserCoupon(long userId, long orderId);
    UserCoupon getUserCoupon(long userId, long orderId, long userCouponId);
    BigDecimal calcTotalFee(BigDecimal totalFee, Coupon coupon);
    UserCoupon getNotUsedUserCouponByOrder(long orderId);
}
