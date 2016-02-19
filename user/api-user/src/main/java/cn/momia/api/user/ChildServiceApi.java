package cn.momia.api.user;

import cn.momia.api.user.dto.Child;
import cn.momia.api.user.dto.ChildComment;
import cn.momia.api.user.dto.ChildRecord;
import cn.momia.api.user.dto.ChildTag;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import cn.momia.common.core.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ChildServiceApi extends HttpServiceApi {
    public User add(String utoken, String children) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("children", children);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/child"), builder.build()), User.class);
    }

    public Child get(String utoken, long childId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/child/%d", childId), builder.build()), Child.class);
    }

    public List<Child> list(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/child"), builder.build()), Child.class);
    }

    public List<Child> list(Collection<Long> childrenIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("cids", StringUtils.join(childrenIds, ","));
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/child/list"), builder.build()), Child.class);
    }

    public User updateAvatar(String utoken, long childId, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/child/%d/avatar", childId), builder.build()), User.class);
    }

    public User updateName(String utoken, long childId, String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/child/%d/name", childId), builder.build()), User.class);
    }

    public User updateSex(String utoken, long childId, String sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/child/%d/sex", childId), builder.build()), User.class);
    }

    public User updateBirthday(String utoken, long childId, Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", TimeUtil.SHORT_DATE_FORMAT.format(birthday));
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/child/%d/birthday", childId), builder.build()), User.class);
    }

    public User delete(String utoken, long childId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.DELETE(url("/child/%d", childId), builder.build()), User.class);
    }

    public List<ChildTag> listAllTags() {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/child/tag")), ChildTag.class);
    }

    public ChildRecord getRecord(String utoken, long childId, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/child/%d/record", childId), builder.build()), ChildRecord.class);
    }

    public boolean record(String utoken, long childId, long courseId, long courseSkuId, String record) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("record", record);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/child/%d/record", childId), builder.build()), Boolean.class);
    }

    public PagedList<ChildComment> listComments(String utoken, long childId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/child/%d/comment", childId), builder.build()), ChildComment.class);
    }

    public boolean comment(String utoken, long childId, long courseId, long courseSkuId, String comment) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("comment", comment);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/child/%d/comment", childId), builder.build()), Boolean.class);
    }

    public List<Long> queryCommentedChildIds(long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("coid", courseId)
                .add("sid", courseSkuId);
        return executeReturnList(MomiaHttpRequestBuilder.POST(url("/child/comment"), builder.build()), Long.class);
    }
}
