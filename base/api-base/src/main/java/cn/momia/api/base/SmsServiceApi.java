package cn.momia.api.base;

import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

public class SmsServiceApi extends ServiceApi {
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

    public boolean notify(String mobile, String msg) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("msg", msg);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/sms/notify"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
