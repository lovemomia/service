package cn.momia.service.coupon;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface CouponService {
    Coupon get(int couponId);
    List<Coupon> list(Collection<Integer> couponIds);
    List<Coupon> queryBySrc(int src);
    List<Coupon> queryBySrcAndDiscount(int src, int discount);

    BigDecimal calcTotalFee(BigDecimal totalFee, Coupon coupon);
}
