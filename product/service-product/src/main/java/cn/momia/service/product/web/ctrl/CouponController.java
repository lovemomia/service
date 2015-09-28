package cn.momia.service.product.web.ctrl;

import cn.momia.api.product.dto.CouponDto;
import cn.momia.api.user.dto.UserDto;
import cn.momia.api.user.UserServiceApi;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.api.dto.PagedList;
import cn.momia.service.order.Order;
import cn.momia.service.order.OrderService;
import cn.momia.service.coupon.Coupon;
import cn.momia.service.coupon.UserCoupon;
import cn.momia.service.promo.facade.PromoServiceFacade;
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
    @Autowired private OrderService orderService;
    @Autowired private PromoServiceFacade promoServiceFacade;

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse coupon(@RequestParam String utoken,
                                    @RequestParam(value = "oid") long orderId,
                                    @RequestParam(value = "coupon") long userCouponId) {
        UserDto user = UserServiceApi.USER.get(utoken);

        Order order = orderService.get(orderId);
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
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        UserDto user = UserServiceApi.USER.get(utoken);

        BigDecimal totalFee = new BigDecimal(0);
        if (orderId > 0) {
            Order order = orderService.get(orderId);
            if (order.exists()) totalFee = order.getTotalFee();
        }

        int totalCount = promoServiceFacade.queryUserCouponCount(user.getId(), orderId, totalFee, status);
        List<UserCoupon> userCoupons = promoServiceFacade.queryUserCoupon(user.getId(), orderId, totalFee, status, start, count);

        List<Integer> couponIds = new ArrayList<Integer>();
        for (UserCoupon userCoupon : userCoupons) couponIds.add(userCoupon.getCouponId());
        List<Coupon> coupons = promoServiceFacade.listCoupons(couponIds);

        return MomiaHttpResponse.SUCCESS(buildPagedCouponDtos(totalCount, userCoupons, coupons, start, count));
    }

    private PagedList<CouponDto> buildPagedCouponDtos(int totalCount, List<UserCoupon> userCoupons, List<Coupon> coupons, int start, int count) {
        PagedList<CouponDto> pagedCouponDtos = new PagedList(totalCount, start, count);
        Map<Integer, Coupon> couponsMap = new HashMap<Integer, Coupon>();
        for (Coupon coupon : coupons) couponsMap.put(coupon.getId(), coupon);

        List<CouponDto> couponDtos = new ArrayList<CouponDto>();
        for (UserCoupon userCoupon : userCoupons) {
            Coupon coupon = couponsMap.get(userCoupon.getCouponId());
            if (coupon == null) continue;

            CouponDto couponDto = new CouponDto();
            couponDto.setId(userCoupon.getId());
            couponDto.setCouponId(userCoupon.getCouponId());
            couponDto.setType(coupon.getType());
            couponDto.setTitle(coupon.getTitle());
            couponDto.setDesc(coupon.getDesc());
            couponDto.setDiscount(coupon.getDiscount());
            couponDto.setConsumption(coupon.getConsumption());
            couponDto.setStartTime(userCoupon.getStartTime());
            couponDto.setEndTime(userCoupon.getEndTime());
            couponDto.setStatus(userCoupon.getStatus());

            couponDtos.add(couponDto);
        }
        pagedCouponDtos.setList(couponDtos);

        return pagedCouponDtos;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public MomiaHttpResponse registerCoupon(@RequestParam String utoken) {
        UserDto user = UserServiceApi.USER.get(utoken);
        promoServiceFacade.distributeRegisterCoupon(user.getId());

        return MomiaHttpResponse.SUCCESS;
    }
}