package cn.momia.api.course;

import cn.momia.api.course.dto.PaymentDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Map;

public class PaymentServiceApi extends ServiceApi {
    public Object prepayAlipay(String utoken, long orderId, String type, long userCouponId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("type", type)
                .add("coupon", userCouponId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/payment/prepay/alipay"), builder.build());

        return executeRequest(request);
    }

    public Object prepayWeixin(String utoken, long orderId, String type, String code, long userCouponId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("type", type)
                .add("coupon", userCouponId);
        if (!StringUtils.isBlank(code)) builder.add("code", code);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/payment/prepay/weixin"), builder.build());

        return executeRequest(request);
    }

    public boolean callbackAlipay(Map<String, String> params) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/payment/callback/alipay"), params);
        return "OK".equalsIgnoreCase((String) executeRequest(request));
    }

    public boolean callbackWeixin(Map<String, String> params) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/payment/callback/weixin"), params);
        return "OK".equalsIgnoreCase((String) executeRequest(request));
    }

    public PaymentDto checkPayment(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/payment/check"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), PaymentDto.class);
    }
}
