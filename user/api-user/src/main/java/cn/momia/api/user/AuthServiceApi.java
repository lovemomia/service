package cn.momia.api.user;

import cn.momia.api.user.dto.User;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;

public class AuthServiceApi extends HttpServiceApi {
    public User register(String nickName, String mobile, String password, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("nickname", nickName)
                .add("mobile", mobile)
                .add("password", password)
                .add("code", code);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/auth/register"), builder.build()), User.class);
    }

    public User login(String mobile, String password) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("password", password);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/auth/login"), builder.build()), User.class);
    }

    public User loginByCode(String mobile, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("code", code);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/auth/login/code"), builder.build()), User.class);
    }

    public User updatePassword(String mobile, String password, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("password", password)
                .add("code", code);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/auth/password"), builder.build()), User.class);
    }
}
