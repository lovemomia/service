package cn.momia.service.course.subject.coupon;

import java.util.List;

public interface CouponService {
    long queryCount(long userId, int status);
    List<UserCoupon> query(long userId, int status, int start, int count);
}
