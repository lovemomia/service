package cn.momia.service.course.subject.coupon;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    UserCoupon get(long userCouponId);

    long queryCount(long userId, int status);
    List<UserCoupon> query(long userId, int status, int start, int count);

    BigDecimal calcTotalFee(BigDecimal totalFee, UserCoupon userCoupon);

    boolean useCoupon(long orderId, long userCouponId);
}
