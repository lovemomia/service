package cn.momia.api.course;

import cn.momia.api.course.dto.coupon.Coupon;
import cn.momia.api.course.dto.coupon.CouponCode;
import cn.momia.api.course.dto.coupon.UserCoupon;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;

import java.math.BigDecimal;
import java.util.List;

public class CouponServiceApi extends HttpServiceApi {
    public Coupon get(int couponId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/coupon/%d", couponId)), Coupon.class);
    }

    public BigDecimal coupon(String utoken, long orderId, long userCouponId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("coupon", userCouponId);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/coupon"), builder.build()), BigDecimal.class);
    }

    public void invite(String mobile, String inviteCode) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("invite", inviteCode);
        execute(MomiaHttpRequestBuilder.POST(url("/coupon/invite"), builder.build()));
    }

    public void distributeInviteCoupon(long userId, String mobile) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("mobile", mobile);
        execute(MomiaHttpRequestBuilder.POST(url("/coupon/invite/distribute"), builder.build()));
    }

    public PagedList<UserCoupon> listUserCoupons(String utoken, int status, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/coupon/list"), builder.build()), UserCoupon.class);
    }

    public List<UserCoupon> queryUserCouponsToExpired(int days) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("days", days);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/coupon/expired"), builder.build()), UserCoupon.class);
    }

    public CouponCode couponCode(String code) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/coupon/code/%s", code)), CouponCode.class);
    }
}
