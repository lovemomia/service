package cn.momia.service.promo.coupon;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface CouponService {
    Coupon getCoupon(int couponId);
    List<Coupon> getCoupons(Collection<Integer> couponIds);
    long getUserRegisterCoupon(long userId);
    int queryCountByUser(long userId, int status);
    List<UserCoupon> queryByUser(long userId, int status, int start, int count);
    boolean lockUserCoupon(long id, long orderId, long userCouponId);
    BigDecimal calcTotalFee(BigDecimal totalFee, Coupon coupon);
}
