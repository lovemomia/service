package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.CouponService;
import cn.momia.service.promo.coupon.UserCoupon;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/coupon")
public class CouponController {
    @Autowired private UserService userService;
    @Autowired private OrderService orderService;
    @Autowired private CouponService couponService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage coupon(@RequestParam String utoken, @RequestParam(value = "oid") long orderId, @RequestParam(value = "coupon") long userCouponId) {
        if (StringUtils.isBlank(utoken) || orderId <= 0 || userCouponId <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = orderService.get(orderId);
        if (!order.exists()) return ResponseMessage.BAD_REQUEST;

        UserCoupon userCoupon = couponService.getUserCoupon(user.getId(), order.getId(), userCouponId);
        if (!userCoupon.exists()) return ResponseMessage.FAILED("无效的优惠券，或使用条件不满足");

        Coupon coupon = couponService.getCoupon(userCoupon.getCouponId());
        if (!coupon.exists()) return ResponseMessage.FAILED("无效的优惠券，或使用条件不满足");

        BigDecimal totalFee = order.getTotalFee();
        if (coupon.getConsumption().compareTo(totalFee) > 0) return ResponseMessage.FAILED("使用条件不满足，无法使用");

        return new ResponseMessage(couponService.calcTotalFee(totalFee, coupon));
    }
}
