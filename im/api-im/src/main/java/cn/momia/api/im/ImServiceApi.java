package cn.momia.api.im;

import cn.momia.api.im.dto.Group;
import cn.momia.api.im.dto.GroupMember;
import cn.momia.api.im.dto.UserGroup;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;

public class ImServiceApi extends HttpServiceApi {
    public String generateImToken(long userId, String nickName, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("nickname", nickName)
                .add("avatar", avatar);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/im/user/token"), builder.build()), String.class);
    }

    public void updateImNickName(long userId, String nickName) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("nickname", nickName);
        execute(MomiaHttpRequestBuilder.PUT(url("/im/user/nickname"), builder.build()));
    }

    public void updateImAvatar(long userId, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("avatar", avatar);
        execute(MomiaHttpRequestBuilder.PUT(url("/im/user/avatar"), builder.build()));
    }

    public List<UserGroup> listUserGroups(long userId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/im/user/group"), builder.build()), UserGroup.class);
    }

    public boolean createGroup(long courseId, long courseSkuId, Collection<Long> teacherUserIds, String groupName) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("tids", StringUtils.join(teacherUserIds, ","))
                .add("name", groupName);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/im/group"), builder.build()), Boolean.class);
    }

    public boolean updateGroup(long courseId, long courseSkuId, String groupName) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("name", groupName);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/im/group"), builder.build()), Boolean.class);
    }

    public boolean dismissGroup(long groupId) {
        return executeReturnObject(MomiaHttpRequestBuilder.DELETE(url("/im/group/%d", groupId)), Boolean.class);
    }

    public Group getGroup(long groupId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/im/group/%d", groupId)), Group.class);
    }

    public List<GroupMember> listGroupMembers(long userId, long groupId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/im/group/%d/member", groupId), builder.build()), GroupMember.class);
    }

    public boolean joinGroup(long userId, long courseId, long courseSkuId, boolean teacher) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("teacher", teacher);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/im/group/join"), builder.build()), Boolean.class);
    }

    public boolean leaveGroup(long userId, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/im/group/leave"), builder.build()), Boolean.class);
    }

    public boolean push(long userId, String content, String extra) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("content", content)
                .add("extra", extra);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/im/push"), builder.build()), Boolean.class);
    }

    public boolean pushBatch(Collection<Long> userIds, String content, String extra) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uids", StringUtils.join(userIds, ","))
                .add("content", content)
                .add("extra", extra);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/im/push/batch"), builder.build()), Boolean.class);
    }

    public boolean pushGroup(long groupId, String content, String extra) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("gid", groupId)
                .add("content", content)
                .add("extra", extra);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/im/push/group"), builder.build()), Boolean.class);
    }
}
