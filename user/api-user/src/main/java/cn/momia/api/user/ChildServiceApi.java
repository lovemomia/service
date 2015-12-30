package cn.momia.api.user;

import cn.momia.api.user.dto.Child;
import cn.momia.api.user.dto.ChildRecord;
import cn.momia.api.user.dto.ChildTag;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import cn.momia.common.core.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ChildServiceApi extends HttpServiceApi {
    public User add(String utoken, String children) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("children", children);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/child"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public Child get(String utoken, long childId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/child/%d", childId), builder.build());

        return executeReturnObject(request, Child.class);
    }

    public List<Child> list(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/child"), builder.build());

        return executeReturnList(request, Child.class);
    }

    public List<Child> list(Collection<Long> childrenIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("cids", StringUtils.join(childrenIds, ","));
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/child/list"), builder.build());

        return executeReturnList(request, Child.class);
    }

    public User updateAvatar(String utoken, long childId, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/child/%d/avatar", childId), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateName(String utoken, long childId, String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/child/%d/name", childId), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateSex(String utoken, long childId, String sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/child/%d/sex", childId), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateBirthday(String utoken, long childId, Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", TimeUtil.SHORT_DATE_FORMAT.format(birthday));
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/child/%d/birthday", childId), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User delete(String utoken, long childId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/child/%d", childId), builder.build());

        return executeReturnObject(request, User.class);
    }

    public List<ChildTag> listAllTags() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/child/tag"));
        return executeReturnList(request, ChildTag.class);
    }

    public ChildRecord getRecord(String utoken, long childId, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/child/%d/record", childId), builder.build());

        return executeReturnObject(request, ChildRecord.class);
    }

    public boolean record(String utoken, long childId, long courseId, long courseSkuId, String record) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("record", record);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/child/%d/record", childId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
