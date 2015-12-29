package cn.momia.api.course;

import cn.momia.api.course.dto.PaymentResult;
import cn.momia.common.core.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Map;

public class PaymentServiceApi extends HttpServiceApi {
    public Object prepayAlipay(String utoken, long orderId, String type, long userCouponId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("type", type)
                .add("coupon", userCouponId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/payment/prepay/alipay"), builder.build());

        return execute(request);
    }

    public Object prepayWeixin(String utoken, long orderId, String type, String code, long userCouponId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("type", type)
                .add("coupon", userCouponId);
        if (!StringUtils.isBlank(code)) builder.add("code", code);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/payment/prepay/weixin"), builder.build());

        return execute(request);
    }

    public boolean callbackAlipay(Map<String, String> params) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/payment/callback/alipay"), params);
        return "OK".equalsIgnoreCase(executeReturnObject(request, String.class));
    }

    public boolean callbackWeixin(Map<String, String> params) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/payment/callback/weixin"), params);
        return "OK".equalsIgnoreCase(executeReturnObject(request, String.class));
    }

    public PaymentResult checkPayment(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/payment/check"), builder.build());

        return executeReturnObject(request, PaymentResult.class);
    }
}
