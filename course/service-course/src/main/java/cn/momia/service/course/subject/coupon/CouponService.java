package cn.momia.service.course.subject.coupon;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    boolean hasRegisterCoupon(long userId);

    UserCoupon get(long userCouponId);

    long queryCount(long userId, int status);
    List<UserCoupon> query(long userId, int status, int start, int count);

    UserCoupon queryByOrder(long orderId);

    BigDecimal calcTotalFee(BigDecimal totalFee, UserCoupon userCoupon);

    boolean preUseCoupon(long orderId, long userCouponId);
    boolean useCoupon(long orderId, long userCouponId);
}
