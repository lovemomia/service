package cn.momia.api.user;

import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

public class SmsServiceApi extends HttpServiceApi {
    public boolean send(String mobile) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("mobile", mobile);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/sms/send"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean verify(String mobile, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("code", code);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/sms/verify"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
