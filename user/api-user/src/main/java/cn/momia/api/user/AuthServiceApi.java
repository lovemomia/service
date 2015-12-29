package cn.momia.api.user;

import cn.momia.api.user.dto.User;
import cn.momia.common.api.HttpServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

public class AuthServiceApi extends HttpServiceApi {
    public User register(String nickName, String mobile, String password, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("nickname", nickName)
                .add("mobile", mobile)
                .add("password", password)
                .add("code", code);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/auth/register"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User login(String mobile, String password) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("password", password);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/auth/login"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updatePassword(String mobile, String password, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("password", password)
                .add("code", code);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/auth/password"), builder.build());

        return executeReturnObject(request, User.class);
    }
}
