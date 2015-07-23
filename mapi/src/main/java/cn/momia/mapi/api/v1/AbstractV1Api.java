package cn.momia.mapi.api.v1;

import cn.momia.common.web.exception.MomiaExpiredException;
import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.AbstractApi;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.user.UserDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;

public class AbstractV1Api extends AbstractApi {
    protected Function<Object, Dto> userFunc = new Function<Object, Dto>() {
        @Override
        public Dto apply(Object data) {
            return JSON.toJavaObject((JSON) data, UserDto.class);
        }
    };

    protected long getUserId(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("user"), builder.build());

        ResponseMessage response = executeRequest(request);
        if (response.successful()) return ((JSONObject) response.getData()).getLong("id");

        throw new MomiaExpiredException();
    }
}
