package cn.momia.api.discuss;

import cn.momia.api.discuss.dto.DiscussReply;
import cn.momia.api.discuss.dto.DiscussTopic;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;

import java.util.Collection;
import java.util.List;

public class DiscussServiceApi extends HttpServiceApi {
    public PagedList<DiscussTopic> listTopics(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/discuss/topic/list"), builder.build()), DiscussTopic.class);
    }

    public DiscussTopic getTopic(int topicId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/discuss/topic/%d", topicId)), DiscussTopic.class);
    }

    public PagedList<DiscussReply> listReplies(int topicId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/discuss/topic/%d/reply", topicId), builder.build()), DiscussReply.class);
    }

    public boolean reply(long userId, int topicId, String content) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("content", content);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/discuss/topic/%d/reply", topicId), builder.build()), Boolean.class);
    }

    public List<Long> filterNotStaredReplyIds(long userId, Collection<Long> replyIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("replyids", replyIds);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/discuss/reply/filter/notstared"), builder.build()), Long.class);
    }

    public boolean star(long userId, long replyId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/discuss/reply/%d/star", replyId), builder.build()), Boolean.class);
    }

    public boolean unstar(long userId, long replyId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/discuss/reply/%d/unstar", replyId), builder.build()), Boolean.class);
    }
}
