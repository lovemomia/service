package cn.momia.service.promo.facade;

import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.UserCoupon;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface PromoServiceFacade {
    void distributeRegisterCoupon(long userId);
    void distributeShareCoupon(long customerId, long sharerId, BigDecimal totalFee);

    Coupon getCoupon(long userId, long orderId, long userCouponId);
    List<Coupon> listCoupons(Collection<Integer> couponIds);
    boolean canUse(BigDecimal totalFee, Coupon coupon);
    BigDecimal calcTotalFee(BigDecimal totalFee, Coupon coupon);

    int queryUserCouponCount(long userId, long orderId, BigDecimal totalFee, int status);
    List<UserCoupon> queryUserCoupon(long userId, long orderId, BigDecimal totalFee, int status, int start, int count);

    UserCoupon getNotUsedUserCouponByOrder(long orderId);
    boolean lockUserCoupon(long userId, long orderId, long userCouponId);
    boolean useUserCoupon(long userId, long orderId, long userCouponId);
    boolean releaseUserCoupon(long userId, long orderId);
}
