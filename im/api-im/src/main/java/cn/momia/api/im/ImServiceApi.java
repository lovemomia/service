package cn.momia.api.im;

import cn.momia.api.im.dto.ImUser;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class ImServiceApi extends ServiceApi {
    public String getImToken(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/token"), builder.build());

        return executeReturnObject(request, String.class);
    }

    public ImUser getImUser(long userId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/user/{uid}", userId));
        return executeReturnObject(request, ImUser.class);
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

    public List<ImUser> listGroupMembers(String utoken, long groupId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/im/group/%d/member", groupId), builder.build());

        return executeReturnList(request, ImUser.class);
    }
}
