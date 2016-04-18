package cn.momia.api.course;

import cn.momia.api.course.activity.Activity;
import cn.momia.api.course.activity.ActivityEntry;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ActivityServiceApi extends HttpServiceApi {
    public Activity get(int activityId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/activity/%d", activityId)), Activity.class);
    }

    public ActivityEntry getEntry(long entryId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/activity/entry/%d", entryId)), ActivityEntry.class);
    }

    public ActivityEntry getEntry(int activityId, String mobile, String childName) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("cname", childName);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/activity/%d/entry", activityId), builder.build()), ActivityEntry.class);
    }

    public long join(int activityId, String mobile, String childName, String relation, String extra) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("cname", childName)
                .add("relation", relation)
                .add("extra", extra);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/activity/%d/join", activityId), builder.build()), Number.class).longValue();
    }

    public Object prepayAlipay(long entryId, String type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("eid", entryId)
                .add("type", type);
        return execute(MomiaHttpRequestBuilder.POST(url("/activity/payment/prepay/alipay"), builder.build()));
    }

    public Object prepayWeixin(long entryId, String type, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("eid", entryId)
                .add("type", type);
        if (!StringUtils.isBlank(code)) builder.add("code", code);
        return execute(MomiaHttpRequestBuilder.POST(url("/activity/payment/prepay/weixin"), builder.build()));
    }

    public boolean callbackAlipay(Map<String, String> params) {
        return "OK".equalsIgnoreCase(executeReturnObject(MomiaHttpRequestBuilder.POST(url("/activity/payment/callback/alipay"), params), String.class));
    }

    public boolean callbackWeixin(Map<String, String> params) {
        return "OK".equalsIgnoreCase(executeReturnObject(MomiaHttpRequestBuilder.POST(url("/activity/payment/callback/weixin"), params), String.class));
    }

    public boolean checkPayment(long entryId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("eid", entryId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/activity/payment/check"), builder.build()), Boolean.class);
    }

    public Object getCoupon(String utoken, int couponId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coupon", couponId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST("/activity/coupon", builder.build()), Object.class);
    }
}
