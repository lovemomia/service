package cn.momia.service.deal.web.ctrl;

import cn.momia.api.user.User;
import cn.momia.api.user.UserServiceApi;
import cn.momia.service.base.web.ctrl.AbstractController;
import cn.momia.service.deal.facade.DealServiceFacade;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.web.ctrl.dto.UserCouponDto;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.base.web.ctrl.dto.PagedListDto;
import cn.momia.service.base.web.response.ResponseMessage;
import cn.momia.service.promo.facade.PromoServiceFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired private DealServiceFacade dealServiceFacade;
    @Autowired private PromoServiceFacade promoServiceFacade;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage coupon(@RequestParam String utoken,
                                  @RequestParam(value = "oid") long orderId,
                                  @RequestParam(value = "coupon") long userCouponId) {
        User user = UserServiceApi.USER.get(utoken);

        Order order = dealServiceFacade.getOrder(orderId);
        if (!order.exists() || order.isPayed() || order.getCustomerId() != user.getId()) return ResponseMessage.FAILED("无效的订单");

        Coupon coupon = promoServiceFacade.getCoupon(user.getId(), order.getId(), userCouponId);
        if (!coupon.exists()) return ResponseMessage.FAILED("无效的优惠券，或使用条件不满足");

        BigDecimal totalFee = order.getTotalFee();
        if (!promoServiceFacade.canUse(totalFee, coupon)) return ResponseMessage.FAILED("使用条件不满足，无法使用");

        return ResponseMessage.SUCCESS(promoServiceFacade.calcTotalFee(totalFee, coupon));
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseMessage listCoupons(@RequestParam String utoken,
                                       @RequestParam(value = "oid") long orderId,
                                       @RequestParam int status,
                                       @RequestParam int start,
                                       @RequestParam int count) {
        if (isInvalidLimit(start, count)) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        User user = UserServiceApi.USER.get(utoken);

        int totalCount = promoServiceFacade.queryUserCouponCount(user.getId(), orderId, status);
        List<UserCoupon> userCoupons = promoServiceFacade.queryUserCoupon(user.getId(), orderId, status, start, count);

        List<Integer> couponIds = new ArrayList<Integer>();
        for (UserCoupon userCoupon : userCoupons) couponIds.add(userCoupon.getCouponId());
        List<Coupon> coupons = promoServiceFacade.getCoupons(couponIds);

        return ResponseMessage.SUCCESS(buildUserCoupons(totalCount, userCoupons, coupons, start, count));
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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseMessage registerCoupon(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        User user = UserServiceApi.USER.get(utoken);
        promoServiceFacade.distributeRegisterCoupon(user.getId());

        return ResponseMessage.SUCCESS;
    }
}
