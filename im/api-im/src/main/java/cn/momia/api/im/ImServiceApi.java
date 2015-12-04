package cn.momia.api.im;

import cn.momia.api.im.dto.Group;
import cn.momia.api.im.dto.ImUser;
import cn.momia.api.im.dto.Member;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;
import java.util.Set;

public class ImServiceApi extends ServiceApi {
    public void generateImToken(String utoken, String nickName, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("nickname", nickName)
                .add("avatar", avatar);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/im/token"), builder.build());
        execute(request);
    }

    public String getImToken(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/token"), builder.build());

        return executeReturnObject(request, String.class);
    }

    public void updateImNickName(String utoken, String nickName) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("nickname", nickName);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/im/user/nickname"), builder.build());
        execute(request);
    }

    public void updateImAvatar(String utoken, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/im/user/avatar"), builder.build());
        execute(request);
    }

    public ImUser getImUser(long userId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/user/%d", userId));
        return executeReturnObject(request, ImUser.class);
    }

    public List<Member> queryMembersByUser(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/user/member"), builder.build());

        return executeReturnList(request, Member.class);
    }

    public boolean createGroup(long courseId, long courseSkuId, List<Long> teacherUserIds, String groupName) {
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

    public List<Group> listGroups(Set<Long> groupIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("gids", StringUtils.join(groupIds, ","));
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/group/list"), builder.build());

        return executeReturnList(request, Group.class);
    }

    public List<ImUser> listGroupMembers(String utoken, long groupId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/group/%d/member", groupId), builder.build());

        return executeReturnList(request, ImUser.class);
    }

    public boolean joinGroup(long courseId, long courseSkuId, String userId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/im/group/join"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean leaveGroup(long courseId, long courseSkuId, String userId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/im/group/leave"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
