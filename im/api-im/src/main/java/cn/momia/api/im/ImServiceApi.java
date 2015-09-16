package cn.momia.api.im;

import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;

public class ImServiceApi extends AbstractServiceApi {
    public static ImServiceApi IM = new ImServiceApi();

    public void init() {
        IM.setService(service);
    }

    public String getToken(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("im/token"), builder.build());

        return (String) executeRequest(request);
    }
}
