package cn.momia.api.course;

import cn.momia.api.course.dto.UserCoupon;
import cn.momia.common.api.HttpServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.math.BigDecimal;

public class CouponServiceApi extends HttpServiceApi {
    public BigDecimal coupon(String utoken, long orderId, long userCouponId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("coupon", userCouponId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/coupon"), builder.build());

        return executeReturnObject(request, BigDecimal.class);
    }

    public void invite(String mobile, String inviteCode) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("invite", inviteCode);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/coupon/invite"), builder.build());
        execute(request);
    }

    public void distributeInviteCoupon(long userId, String mobile) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("mobile", mobile);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/coupon/invite/distribute"), builder.build());
        execute(request);
    }

    public PagedList<UserCoupon> listUserCoupons(String utoken, int status, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/coupon/list"), builder.build());

        return executeReturnPagedList(request, UserCoupon.class);
    }
}
