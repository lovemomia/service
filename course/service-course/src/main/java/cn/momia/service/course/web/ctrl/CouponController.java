package cn.momia.service.course.web.ctrl;

import cn.momia.api.im.ImServiceApi;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.coupon.CouponService;
import cn.momia.service.course.coupon.InviteCoupon;
import cn.momia.api.course.dto.coupon.UserCoupon;
import cn.momia.service.course.order.Order;
import cn.momia.service.course.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/coupon")
public class CouponController extends BaseController {
    @Autowired private CouponService couponService;
    @Autowired private OrderService orderService;
    @Autowired private ImServiceApi imServiceApi;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse coupon(@RequestParam String utoken,
                                    @RequestParam(value = "oid") long orderId,
                                    @RequestParam(value = "coupon") long userCouponId) {
        User user = userServiceApi.get(utoken);
        UserCoupon userCoupon = couponService.get(userCouponId);
        if (!userCoupon.exists() || userCoupon.getUserId() != user.getId() || userCoupon.isUsed()) return MomiaHttpResponse.FAILED("无效的红包/优惠券");
        if (userCoupon.isExpired()) return MomiaHttpResponse.FAILED("红包/优惠券已经过期");

        Order order = orderService.get(orderId);
        if (!order.exists() || order.isPayed()) return MomiaHttpResponse.FAILED("无效的订单");

        BigDecimal originalTotalFee = order.getTotalFee();
        if (originalTotalFee.compareTo(userCoupon.getConsumption()) < 0) return MomiaHttpResponse.FAILED("使用条件不满足，无法使用");

        return MomiaHttpResponse.SUCCESS(couponService.calcTotalFee(originalTotalFee, userCoupon));
    }

    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public MomiaHttpResponse inviteCoupon(@RequestParam String mobile, @RequestParam(value = "invite") String inviteCode) {
        if (couponService.hasInviteCoupon(mobile)) return MomiaHttpResponse.FAILED("您已经领取过红包了，不能再领了");
        if (!couponService.addInviteCoupon(mobile, inviteCode)) return MomiaHttpResponse.FAILED("领取失败，可能是该手机号已经领取过了");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/invite/distribute", method = RequestMethod.POST)
    public MomiaHttpResponse inviteUserCoupon(@RequestParam(value = "uid") long userId, @RequestParam String mobile) {
        InviteCoupon inviteCoupon = couponService.getInviteCoupon(mobile);
        if (inviteCoupon.exists() && couponService.updateInviteCouponStatus(mobile)) {
            UserCoupon userCoupon = couponService.distributeInviteUserCoupon(userId, inviteCoupon.getCouponId(), inviteCoupon.getInviteCode());
            if (userCoupon.exists()) imServiceApi.push(userId, String.format(Configuration.getString("PushMsg.Coupon"), userCoupon.getDiscount(), userCoupon.getEndTime()), "");
        }

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public MomiaHttpResponse listCoupons(@RequestParam String utoken,
                                         @RequestParam int status,
                                         @RequestParam int start,
                                         @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        User user = userServiceApi.get(utoken);
        long totalCount = couponService.queryCount(user.getId(), status);
        List<UserCoupon> userCoupons = couponService.query(user.getId(), status, start, count);

        PagedList<UserCoupon> pagedUserCoupons = new PagedList<UserCoupon>(totalCount, start, count);
        pagedUserCoupons.setList(userCoupons);

        return MomiaHttpResponse.SUCCESS(pagedUserCoupons);
    }

    @RequestMapping(value = "/expired", method = RequestMethod.GET)
    public MomiaHttpResponse queryUserCouponsToExpired(@RequestParam int days) {
        return MomiaHttpResponse.SUCCESS(couponService.queryUserCouponsToExpired(days));
    }

    @RequestMapping(value = "/code/{code}", method = RequestMethod.GET)
    public MomiaHttpResponse couponCode(@PathVariable String code) {
        return MomiaHttpResponse.SUCCESS(couponService.getCouponCode(code));
    }
}
