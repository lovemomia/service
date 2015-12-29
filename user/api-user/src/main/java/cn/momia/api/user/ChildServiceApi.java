package cn.momia.api.user;

import cn.momia.api.user.dto.Child;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import cn.momia.common.core.util.TimeUtil;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Date;
import java.util.List;

public class ChildServiceApi extends HttpServiceApi {
    public User add(String utoken, String children) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("children", children);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/user/child"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public Child get(String utoken, long childId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/user/child/%d", childId), builder.build());

        return executeReturnObject(request, Child.class);
    }

    public List<Child> list(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/user/child"), builder.build());

        return executeReturnList(request, Child.class);
    }

    public User updateAvatar(String utoken, long childId, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/child/%d/avatar", childId), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateName(String utoken, long childId, String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/child/%d/name", childId), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateSex(String utoken, long childId, String sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/child/%d/sex", childId), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateBirthday(String utoken, long childId, Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", TimeUtil.SHORT_DATE_FORMAT.format(birthday));
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/child/%d/birthday", childId), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User delete(String utoken, long childId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/user/child/%d", childId), builder.build());

        return executeReturnObject(request, User.class);
    }
}
