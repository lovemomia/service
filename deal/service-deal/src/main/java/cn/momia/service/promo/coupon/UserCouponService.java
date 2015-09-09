package cn.momia.service.promo.coupon;

import java.math.BigDecimal;
import java.util.List;

public interface UserCouponService {
    void add(List<Object[]> params);

    UserCoupon query(long userId, long orderId, long id);

    int queryCountByUser(long userId, long orderId, BigDecimal totalFee, int status);
    List<UserCoupon> queryByUser(long userId, long orderId, BigDecimal totalFee, int status, int start, int count);
    int queryCountByUserAndSrc(long userId, int src);

    UserCoupon queryNotUsedByOrder(long orderId);

    boolean lock(long userId, long orderId, long id);
    boolean use(long userId, long orderId, long id);
    boolean release(long userId, long orderId);
}
