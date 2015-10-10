package cn.momia.api.base;

import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;

public class SmsServiceApi extends AbstractServiceApi {
    public boolean send(String mobile, String type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("mobile", mobile);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("sms/send"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean verify(String mobile, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("code", code);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("sms/verify"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean notify(String mobile, String msg) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("msg", msg);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("sms/notify"), builder.build());

        return (Boolean) executeRequest(request);
    }
}
