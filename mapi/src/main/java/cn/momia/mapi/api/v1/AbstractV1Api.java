package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.AbstractApi;
import com.alibaba.fastjson.JSONObject;

public class AbstractV1Api extends AbstractApi {

    protected long getUserId(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("user"), builder.build());

        ResponseMessage responseMessage = executeRequest(request);
        if (responseMessage.getErrno() == ErrorCode.SUCCESS) return ((JSONObject) responseMessage.getData()).getJSONObject("user").getLong("id");
        if (responseMessage.getErrno() == ErrorCode.TOKEN_EXPIRED) return 0;

        throw new RuntimeException("fail to get user id");
    }
}
