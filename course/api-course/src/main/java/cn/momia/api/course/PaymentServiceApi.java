package cn.momia.api.course;

import cn.momia.api.course.dto.PaymentDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class PaymentServiceApi extends ServiceApi {
    public Object prepayAlipay(String utoken, long orderId, String type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("type", type);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("payment/prepay/alipay"), builder.build());

        return executeRequest(request);
    }

    public Object prepayWeixin(String utoken, long orderId, String type, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("type", type);
        if (!StringUtils.isBlank(code)) builder.add("code", code);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("payment/prepay/weixin"), builder.build());

        return executeRequest(request);
    }

    public boolean callbackAlipay(Map<String, String> params) {
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("payment/callback/alipay"), params);
        return "OK".equalsIgnoreCase((String) executeRequest(request));
    }

    public boolean callbackWeixin(Map<String, String> params) {
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("payment/callback/weixin"), params);
        return "OK".equalsIgnoreCase((String) executeRequest(request));
    }

    public PaymentDto checkPayment(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("payment/check"), builder.build());

        return JSON.toJavaObject((JSON) executeRequest(request), PaymentDto.class);
    }
}
