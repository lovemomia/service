package cn.momia.api.user;

import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;

public class SmsServiceApi extends HttpServiceApi {
    public boolean send(String mobile) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("mobile", mobile);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/sms/send"), builder.build()), Boolean.class);
    }

    public boolean verify(String mobile, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("code", code);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/sms/verify"), builder.build()), Boolean.class);
    }

    public boolean notify(String mobile, String message) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("message", message);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/sms/notify"), builder.build()), Boolean.class);
    }
}
