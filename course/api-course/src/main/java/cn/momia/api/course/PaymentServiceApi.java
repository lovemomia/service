package cn.momia.api.course;

import cn.momia.api.course.dto.subject.PaymentResult;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class PaymentServiceApi extends HttpServiceApi {
    public Object prepayAlipay(String utoken, long orderId, String type, long userCouponId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("type", type)
                .add("coupon", userCouponId);
        return execute(MomiaHttpRequestBuilder.POST(url("/payment/prepay/alipay"), builder.build()));
    }

    public Object prepayWeixin(String utoken, long orderId, String type, String code, long userCouponId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("type", type)
                .add("coupon", userCouponId);
        if (!StringUtils.isBlank(code)) builder.add("code", code);
        return execute(MomiaHttpRequestBuilder.POST(url("/payment/prepay/weixin"), builder.build()));
    }

    public boolean callbackAlipay(Map<String, String> params) {
        return "OK".equalsIgnoreCase(executeReturnObject(MomiaHttpRequestBuilder.POST(url("/payment/callback/alipay"), params), String.class));
    }

    public boolean callbackAlipayRefund(Map<String, String> params) {
        return "OK".equalsIgnoreCase(executeReturnObject(MomiaHttpRequestBuilder.POST(url("/payment/callback/alipay/refund"), params), String.class));
    }

    public boolean callbackWeixin(Map<String, String> params) {
        return "OK".equalsIgnoreCase(executeReturnObject(MomiaHttpRequestBuilder.POST(url("/payment/callback/weixin"), params), String.class));
    }

    public PaymentResult checkPayment(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/payment/check"), builder.build()), PaymentResult.class);
    }
}
