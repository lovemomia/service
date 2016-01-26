package cn.momia.api.discuss;

import cn.momia.api.discuss.dto.DiscussReply;
import cn.momia.api.discuss.dto.DiscussTopic;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

public class DiscussServiceApi extends HttpServiceApi {
    public PagedList<DiscussTopic> listTopics(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/discuss/topic/list"), builder.build());

        return executeReturnPagedList(request, DiscussTopic.class);
    }

    public DiscussTopic getTopic(int topicId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/discuss/topic/%d", topicId));
        return executeReturnObject(request, DiscussTopic.class);
    }

    public PagedList<DiscussReply> listReplies(long userId, int topicId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/discuss/topic/%d/reply", topicId), builder.build());

        return executeReturnPagedList(request, DiscussReply.class);
    }

    public boolean reply(long userId, int topicId, String content) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("content", content);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/discuss/topic/%d/reply", topicId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean star(long userId, long replyId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/discuss/reply/%d/star", replyId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean unstar(long userId, long replyId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/discuss/reply/%d/unstar", replyId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
