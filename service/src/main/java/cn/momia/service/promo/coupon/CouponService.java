package cn.momia.service.promo.coupon;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CouponService {
    Coupon getCoupon(int couponId);
    Map<Integer, Coupon> getCoupons(Collection<Integer> couponIds);
    long getUserRegisterCoupon(long userId);
    int queryCountByUser(long userId, int status);
    List<UserCoupon> queryByUser(long userId, int status, int start, int count);
    boolean lockUserCoupon(long id, long orderId, long userCouponId);
    UserCoupon getUserCoupon(int userCouponId);
    BigDecimal calcTotalFee(BigDecimal totalFee, Coupon coupon);
}
