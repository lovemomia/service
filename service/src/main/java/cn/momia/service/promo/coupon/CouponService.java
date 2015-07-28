package cn.momia.service.promo.coupon;

import cn.momia.common.service.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface CouponService extends Service {
    Coupon getCoupon(int couponId);
    List<Coupon> getCoupons(Collection<Integer> couponIds);
    void getUserRegisterCoupons(long userId);
    int queryCountByUser(long userId, long orderId, int status);
    List<UserCoupon> queryByUser(long userId, long orderId, int status, int start, int count);
    boolean lockUserCoupon(long userId, long orderId, long userCouponId);
    boolean useUserCoupon(long userId, long orderId, long userCouponId);
    boolean releaseUserCoupon(long userId, long orderId);
    UserCoupon getUserCoupon(long userId, long orderId, long userCouponId);
    BigDecimal calcTotalFee(BigDecimal totalFee, Coupon coupon);
    UserCoupon getNotUsedUserCouponByOrder(long orderId);
}
