package cn.momia.api.course;

import cn.momia.api.course.dto.UserCouponDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import org.apache.http.client.methods.HttpUriRequest;

import java.math.BigDecimal;

public class CouponServiceApi extends ServiceApi {
    public BigDecimal coupon(String utoken, long orderId, long userCouponId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("coupon", userCouponId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/coupon"), builder.build());

        return (BigDecimal) executeRequest(request);
    }

    public void invite(String mobile, String inviteCode) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("invite", inviteCode);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/coupon/invite"), builder.build());
        executeRequest(request);
    }

    public void distributeInviteCoupon(long userId, String mobile) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("mobile", mobile);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/coupon/invite/distribute"), builder.build());
        executeRequest(request);
    }

    public PagedList<UserCouponDto> listUserCoupons(String utoken, int status, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/coupon/list"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), UserCouponDto.class);
    }
}
