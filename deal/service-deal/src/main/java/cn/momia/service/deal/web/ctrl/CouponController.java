package cn.momia.service.deal.web.ctrl;

import cn.momia.api.user.User;
import cn.momia.api.user.UserServiceApi;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.webapp.ctrl.dto.PagedListDto;
import cn.momia.service.deal.facade.DealServiceFacade;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.web.ctrl.dto.UserCouponDto;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.UserCoupon;
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
public class CouponController extends BaseController {
    @Autowired private DealServiceFacade dealServiceFacade;
    @Autowired private PromoServiceFacade promoServiceFacade;

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse coupon(@RequestParam String utoken,
                                    @RequestParam(value = "oid") long orderId,
                                    @RequestParam(value = "coupon") long userCouponId) {
        User user = UserServiceApi.USER.get(utoken);

        Order order = dealServiceFacade.getOrder(orderId);
        if (!order.exists() || order.isPayed() || order.getCustomerId() != user.getId()) return MomiaHttpResponse.FAILED("无效的订单");

        Coupon coupon = promoServiceFacade.getCoupon(user.getId(), order.getId(), userCouponId);
        if (!coupon.exists()) return MomiaHttpResponse.FAILED("无效的优惠券，或使用条件不满足");

        BigDecimal totalFee = order.getTotalFee();
        if (!promoServiceFacade.canUse(totalFee, coupon)) return MomiaHttpResponse.FAILED("使用条件不满足，无法使用");

        return MomiaHttpResponse.SUCCESS(promoServiceFacade.calcTotalFee(totalFee, coupon));
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse listCoupons(@RequestParam String utoken,
                                         @RequestParam(value = "oid") long orderId,
                                         @RequestParam int status,
                                         @RequestParam int start,
                                         @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedListDto.EMPTY);

        User user = UserServiceApi.USER.get(utoken);

        BigDecimal totalFee = new BigDecimal(0);
        if (orderId > 0) {
            Order order = dealServiceFacade.getOrder(orderId);
            if (order.exists()) totalFee = order.getTotalFee();
        }

        int totalCount = promoServiceFacade.queryUserCouponCount(user.getId(), orderId, totalFee, status);
        List<UserCoupon> userCoupons = promoServiceFacade.queryUserCoupon(user.getId(), orderId, totalFee, status, start, count);

        List<Integer> couponIds = new ArrayList<Integer>();
        for (UserCoupon userCoupon : userCoupons) couponIds.add(userCoupon.getCouponId());
        List<Coupon> coupons = promoServiceFacade.getCoupons(couponIds);

        return MomiaHttpResponse.SUCCESS(buildUserCoupons(totalCount, userCoupons, coupons, start, count));
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
    public MomiaHttpResponse registerCoupon(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) return MomiaHttpResponse.BAD_REQUEST;

        User user = UserServiceApi.USER.get(utoken);
        promoServiceFacade.distributeRegisterCoupon(user.getId());

        return MomiaHttpResponse.SUCCESS;
    }
}
