package cn.momia.service.promo.coupon;

import java.util.List;

public interface CouponService {
    long getUserRegisterCoupon(long userId);
    int queryCountByUser(long userId, int status);
    List<UserCoupon> queryByUser(long userId, int status, int start, int count);
}
