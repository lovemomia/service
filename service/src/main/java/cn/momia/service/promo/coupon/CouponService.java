package cn.momia.service.promo.coupon;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    Coupon getCoupon(int couponId);
    long getUserRegisterCoupon(long userId);
    int queryCountByUser(long userId, int status);
    List<UserCoupon> queryByUser(long userId, int status, int start, int count);
    UserCoupon getUserCoupon(long id, long userCouponId);
    BigDecimal calcTotalFee(BigDecimal totalFee, Coupon coupon);
}
