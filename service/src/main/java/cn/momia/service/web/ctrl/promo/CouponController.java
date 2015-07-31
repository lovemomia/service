package cn.momia.service.web.ctrl.promo;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.user.base.User;
import cn.momia.service.deal.order.Order;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.web.ctrl.AbstractController;
import cn.momia.service.web.ctrl.dto.PagedListDto;
import cn.momia.service.web.ctrl.promo.dto.UserCouponDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupon")
public class CouponController extends AbstractController {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage coupon(@RequestParam String utoken,
                                  @RequestParam(value = "oid") long orderId,
                                  @RequestParam(value = "coupon") long userCouponId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = dealServiceFacade.getOrder(orderId);
        if (!order.exists() || order.isPayed() || order.getCustomerId() != user.getId()) return ResponseMessage.FAILED("无效的订单");

        Coupon coupon = promoServiceFacade.getCoupon(user.getId(), order.getId(), userCouponId);
        if (!coupon.exists()) return ResponseMessage.FAILED("无效的优惠券，或使用条件不满足");

        BigDecimal totalFee = order.getTotalFee();
        if (!promoServiceFacade.canUse(totalFee, coupon)) return ResponseMessage.FAILED("使用条件不满足，无法使用");

        return new ResponseMessage(promoServiceFacade.calcTotalFee(totalFee, coupon));
    }


    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseMessage getCouponsOfUser(@RequestParam String utoken,
                                            @RequestParam(value = "oid") long orderId,
                                            @RequestParam int status,
                                            @RequestParam int start,
                                            @RequestParam int count) {
        if (isInvalidLimit(start, count)) return new ResponseMessage(PagedListDto.EMPTY);

        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        int totalCount = promoServiceFacade.queryUserCouponCount(user.getId(), orderId, status);
        List<UserCoupon> userCoupons = promoServiceFacade.queryUserCoupon(user.getId(), orderId, status, start, count);

        List<Integer> couponIds = new ArrayList<Integer>();
        for (UserCoupon userCoupon : userCoupons) couponIds.add(userCoupon.getCouponId());
        List<Coupon> coupons = promoServiceFacade.getCoupons(couponIds);

        return new ResponseMessage(buildUserCoupons(totalCount, userCoupons, coupons, start, count));
    }

    private PagedListDto buildUserCoupons(int totalCount, List<UserCoupon> userCoupons, List<Coupon> coupons, int start, int count) {
        PagedListDto userCouponsDto = new PagedListDto(totalCount, start, count);
        Map<Integer, Coupon> couponsMap = new HashMap<Integer, Coupon>();
        for (Coupon coupon : coupons) couponsMap.put(coupon.getId(), coupon);

        for (UserCoupon userCoupon : userCoupons) {
            Coupon coupon = couponsMap.get(userCoupon.getCouponId());
            if (coupon == null) continue;

            UserCouponDto userCouponDto = new UserCouponDto();
            userCouponDto.setId(userCoupon.getId());
            userCouponDto.setCouponId(userCoupon.getCouponId());
            userCouponDto.setType(coupon.getType());
            userCouponDto.setTitle(coupon.getTitle());
            userCouponDto.setDesc(coupon.getDesc());
            userCouponDto.setDiscount(coupon.getDiscount());
            userCouponDto.setConsumption(coupon.getConsumption());
            userCouponDto.setStartTime(userCoupon.getStartTime());
            userCouponDto.setEndTime(userCoupon.getEndTime());
            userCouponDto.setStatus(userCoupon.getStatus());

            userCouponsDto.add(userCouponDto);
        }

        return userCouponsDto;
    }
}
