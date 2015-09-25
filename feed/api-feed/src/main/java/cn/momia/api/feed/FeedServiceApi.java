package cn.momia.api.feed;

import cn.momia.api.feed.dto.FeedCommentDto;
import cn.momia.api.feed.dto.FeedDto;
import cn.momia.api.feed.dto.FeedStarDto;
import cn.momia.api.feed.dto.FeedTopicDto;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class FeedServiceApi extends AbstractServiceApi {
    public static FeedServiceApi FEED = new FeedServiceApi();

    public void init() {
        FEED.setService(service);
    }

    public void follow(long userId, long followedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("fuid", followedId);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("feed/follow"), builder.build());
        executeRequest(request);
    }

    public PagedList<FeedDto> list(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("feed"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), FeedDto.class);
    }

    public PagedList<FeedDto> listByTopic(long userId, long topicId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("tid", topicId)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("feed/topic"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), FeedDto.class);
    }

    public PagedList<FeedTopicDto> listTopic(int type, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("type", type)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("feed/topic/list"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), FeedTopicDto.class);
    }

    public void add(JSONObject feedJson) {
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("feed"), feedJson.toString());
        executeRequest(request);
    }

    public FeedDto get(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("feed", feedId), builder.build());

        return JSON.toJavaObject((JSON) executeRequest(request), FeedDto.class);
    }

    public void delete(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("feed", feedId), builder.build());
        executeRequest(request);
    }

    public PagedList<FeedCommentDto> listComments(long feedId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("feed", feedId, "comment/list"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), FeedCommentDto.class);
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

    public PagedList<FeedStarDto> listStars(long feedId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("feed", feedId, "star/list"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), FeedStarDto.class);
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
}
