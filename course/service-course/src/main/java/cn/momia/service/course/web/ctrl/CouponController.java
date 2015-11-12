package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.UserCouponDto;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.subject.coupon.CouponService;
import cn.momia.service.course.subject.coupon.UserCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/subject/coupon")
public class CouponController extends BaseController {
    @Autowired private CouponService couponService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public MomiaHttpResponse listCoupons(@RequestParam String utoken,
                                         @RequestParam int status,
                                         @RequestParam int start,
                                         @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        UserDto user = userServiceApi.get(utoken);
        long totalCount = couponService.queryCount(user.getId(), status);
        List<UserCoupon> userCoupons = couponService.query(user.getId(), status, start, count);

        PagedList<UserCouponDto> pagedUserCoupons = new PagedList<UserCouponDto>(totalCount, start, count);
        pagedUserCoupons.setList(buildUserCouponDtos(userCoupons));

        return MomiaHttpResponse.SUCCESS(pagedUserCoupons);
    }

    private List<UserCouponDto> buildUserCouponDtos(List<UserCoupon> userCoupons) {
        Date now = new Date();
        List<UserCouponDto> userCouponDtos = new ArrayList<UserCouponDto>();
        for (UserCoupon userCoupon : userCoupons) {
            UserCouponDto userCouponDto = new UserCouponDto();
            userCouponDto.setId(userCoupon.getId());
            userCouponDto.setType(userCoupon.getType());
            userCouponDto.setTitle(userCoupon.getTitle());
            userCouponDto.setDesc(userCoupon.getDesc());
            userCouponDto.setDiscount(userCoupon.getDiscount());
            userCouponDto.setConsumption(userCoupon.getConsumption());
            userCouponDto.setStartTime(userCoupon.getStartTime());
            userCouponDto.setEndTime(userCoupon.getEndTime());
            int status = userCoupon.getStatus();
            if (status == 1 && userCoupon.getEndTime().before(now)) status = 3;
            userCouponDto.setStatus(status);

            userCouponDtos.add(userCouponDto);
        }

        return userCouponDtos;
    }

    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public MomiaHttpResponse inviteCoupon(@RequestParam String mobile, @RequestParam(value = "invite") String inviteCode) {
        if (couponService.hasInviteCoupon(mobile)) return MomiaHttpResponse.FAILED("一个手机号只能领取一次");
        if (!couponService.addInviteCoupon(mobile, inviteCode)) return MomiaHttpResponse.FAILED("领取失败，可能是该手机号已经领取过了");
        return MomiaHttpResponse.SUCCESS;
    }
}
