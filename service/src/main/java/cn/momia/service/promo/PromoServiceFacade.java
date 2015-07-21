package cn.momia.service.promo;

import cn.momia.service.promo.banner.Banner;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.UserCoupon;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface PromoServiceFacade {
    List<Banner> getBanners(int cityId, int count);

    long getUserRegisterCoupon(long userId);

    UserCoupon getUserCoupon(long userId, long orderId, long userCouponId);
    Coupon getCoupon(int couponId);
    BigDecimal calcTotalFee(BigDecimal totalFee, Coupon coupon);

    int queryUserCouponCount(long userId, long orderId, int status);
    List<UserCoupon> queryUserCoupon(long userId, long orderId, int status, int start, int count);
    Map<Integer, Coupon> getCoupons(Collection<Integer> couponIds);

    UserCoupon getNotUsedUserCouponByOrder(long orderId);
    boolean lockUserCoupon(long userId, long orderId, long userCouponId);
    boolean useUserCoupon(long userId, long orderId, long userCouponId);
    boolean releaseUserCoupon(long userId, long orderId);
}
