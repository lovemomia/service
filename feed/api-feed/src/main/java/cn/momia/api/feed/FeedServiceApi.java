package cn.momia.api.feed;

import cn.momia.api.base.ServiceApi;
import cn.momia.api.base.http.MomiaHttpParamBuilder;
import cn.momia.api.base.http.MomiaHttpRequest;
import cn.momia.api.feed.comment.PagedFeedComments;
import cn.momia.api.feed.star.PagedFeedStars;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class FeedServiceApi extends ServiceApi {
    public static FeedServiceApi FEED = new FeedServiceApi();

    public void init() {
        FEED.setService(service);
    }

    public PagedFeeds list(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("feed"), builder.build());

        return JSON.toJavaObject((JSON) executeRequest(request), PagedFeeds.class);
    }

    public PagedFeeds listByTopic(long userId, long topicId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("tid", topicId)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("feed/topic"), builder.build());

        return JSON.toJavaObject((JSON) executeRequest(request), PagedFeeds.class);
    }

    public void add(JSONObject feedJson) {
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("feed"), feedJson.toString());
        executeRequest(request);
    }

    public Feed get(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("feed", feedId), builder.build());

        return JSON.toJavaObject((JSON) executeRequest(request), Feed.class);
    }

    public void delete(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("feed", feedId), builder.build());
        executeRequest(request);
    }

    public PagedFeedComments listComments(long feedId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("feed", feedId, "comment/list"), builder.build());

        return JSON.toJavaObject((JSON) executeRequest(request), PagedFeedComments.class);
    }

    public void addComment(long userId, long feedId, String content) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("content", content);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("feed", feedId, "comment"), builder.build());
        executeRequest(request);
    }

    public void deleteComment(long userId, long feedId, long commentId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("feed", feedId, "comment", commentId), builder.build());
        executeRequest(request);
    }

    public void star(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("feed", feedId, "star"), builder.build());
        executeRequest(request);
    }

    public void unstar(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("feed", feedId, "unstar"), builder.build());
        executeRequest(request);
    }

    public PagedFeedStars listStars(long feedId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("feed", feedId, "star/list"), builder.build());

        return JSON.toJavaObject((JSON) executeRequest(request), PagedFeedStars.class);
    }
}
