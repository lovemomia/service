package cn.momia.mapi.api.v1;

import cn.momia.common.web.exception.MomiaExpiredException;
import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.AbstractApi;
import com.alibaba.fastjson.JSONObject;

public class AbstractV1Api extends AbstractApi {
    protected long getUserId(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("user"), builder.build());

        ResponseMessage response = executeRequest(request);
        if (response.successful()) return ((JSONObject) response.getData()).getJSONObject("user").getLong("id");

        throw new MomiaExpiredException();
    }
}
