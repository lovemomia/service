package cn.momia.service.web.ctrl.promo;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.user.base.User;
import cn.momia.service.deal.order.Order;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
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

        UserCoupon userCoupon = promoServiceFacade.getUserCoupon(user.getId(), order.getId(), userCouponId);
        if (!userCoupon.exists()) return ResponseMessage.FAILED("无效的优惠券，或使用条件不满足");

        Coupon coupon = promoServiceFacade.getCoupon(userCoupon.getCouponId());
        if (!coupon.exists()) return ResponseMessage.FAILED("无效的优惠券，或使用条件不满足");

        BigDecimal totalFee = order.getTotalFee();
        if (coupon.getConsumption().compareTo(totalFee) > 0) return ResponseMessage.FAILED("使用条件不满足，无法使用");

        return new ResponseMessage(promoServiceFacade.calcTotalFee(totalFee, coupon));
    }


    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseMessage getCouponsOfUser(@RequestParam String utoken,
                                            @RequestParam(value = "oid") long orderId,
                                            @RequestParam int status,
                                            @RequestParam int start,
                                            @RequestParam int count) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        int totalCount = promoServiceFacade.queryUserCouponCount(user.getId(), orderId, status);
        List<UserCoupon> userCoupons = totalCount > 0 ? promoServiceFacade.queryUserCoupon(user.getId(), orderId, status, start, count) : new ArrayList<UserCoupon>();

        List<Integer> couponIds = new ArrayList<Integer>();
        for (UserCoupon userCoupon : userCoupons) couponIds.add(userCoupon.getCouponId());
        List<Coupon> coupons = promoServiceFacade.getCoupons(couponIds);

        return new ResponseMessage(buildCoupons(totalCount, userCoupons, coupons));
    }

    private JSONObject buildCoupons(int totalCount, List<UserCoupon> userCoupons, List<Coupon> coupons) {
        JSONObject couponsPackJson = new JSONObject();
        couponsPackJson.put("totalCount", totalCount);
        couponsPackJson.put("userCoupons", userCoupons);
        couponsPackJson.put("coupons", coupons);

        return couponsPackJson;
    }
}
