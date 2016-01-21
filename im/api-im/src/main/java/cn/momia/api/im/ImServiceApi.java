package cn.momia.api.im;

import cn.momia.api.im.dto.Group;
import cn.momia.api.im.dto.GroupMember;
import cn.momia.api.im.dto.UserGroup;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Collection;
import java.util.List;

public class ImServiceApi extends HttpServiceApi {
    public String generateImToken(long userId, String nickName, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("nickname", nickName)
                .add("avatar", avatar);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/im/token"), builder.build());

        return executeReturnObject(request, String.class);
    }

    public void updateImNickName(long userId, String nickName) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("nickname", nickName);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/im/user/nickname"), builder.build());
        execute(request);
    }

    public void updateImAvatar(long userId, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("avatar", avatar);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/im/user/avatar"), builder.build());
        execute(request);
    }

    public boolean createGroup(long courseId, long courseSkuId, Collection<Long> teacherUserIds, String groupName) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("tids", StringUtils.join(teacherUserIds, ","))
                .add("name", groupName);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/im/group"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean updateGroupName(long courseId, long courseSkuId, String groupName) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("name", groupName);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/im/group"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean dismissGroup(long groupId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/im/group/%d", groupId));
        return executeReturnObject(request, Boolean.class);
    }

    public Group getGroup(long groupId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/group/%d", groupId));
        return executeReturnObject(request, Group.class);
    }

    public List<Group> listGroups(Collection<Long> groupIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("gids", StringUtils.join(groupIds, ","));
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/group/list"), builder.build());

        return executeReturnList(request, Group.class);
    }

    public List<GroupMember> listGroupMembers(long userId, long groupId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/group/%d/member", groupId), builder.build());

        return executeReturnList(request, GroupMember.class);
    }

    public boolean joinGroup(long userId, long courseId, long courseSkuId, boolean teacher) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("teacher", teacher);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/im/group/join"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean leaveGroup(long userId, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/im/group/leave"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public List<UserGroup> listUserGroups(long userId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/user/group"), builder.build());

        return executeReturnList(request, UserGroup.class);
    }

    public boolean push(long userId, String content, String extra) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("content", content)
                .add("extra", extra);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/im/push"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean pushBatch(Collection<Long> userIds, String content, String extra) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uids", StringUtils.join(userIds, ","))
                .add("content", content)
                .add("extra", extra);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/im/push/batch"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean pushGroup(long groupId, String content, String extra) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("gid", groupId)
                .add("content", content)
                .add("extra", extra);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/im/push/group"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
