package cn.momia.api.feed;

import cn.momia.api.feed.dto.FeedCommentDto;
import cn.momia.api.feed.dto.FeedDto;
import cn.momia.api.feed.dto.FeedStarDto;
import cn.momia.api.feed.dto.FeedTagsDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpUriRequest;

public class FeedServiceApi extends ServiceApi {
    public void follow(long userId, long followedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("fuid", followedId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feed/follow"), builder.build());
        executeRequest(request);
    }

    public PagedList<FeedDto> list(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), FeedDto.class);
    }

    public PagedList<FeedDto> listFeedsOfUser(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/user"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), FeedDto.class);
    }

    public PagedList<FeedDto> queryByCourse(long userId, long courseId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("coid", courseId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/course"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), FeedDto.class);
    }

    public FeedTagsDto listTags(int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/tag"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), FeedTagsDto.class);
    }

    public void add(JSONObject feedJson) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feed"), feedJson.toString());
        executeRequest(request);
    }

    public FeedDto get(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/%d", feedId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), FeedDto.class);
    }

    public boolean delete(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/feed/%d", feedId), builder.build());

        return (Boolean) executeRequest(request);
    }

    public PagedList<FeedCommentDto> listComments(long feedId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/%d/comment/list", feedId), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), FeedCommentDto.class);
    }

    public void addComment(long userId, long feedId, String content) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("content", content);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feed/%d/comment", feedId), builder.build());
        executeRequest(request);
    }

    public void deleteComment(long userId, long feedId, long commentId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/feed/%d/comment/%d", feedId, commentId), builder.build());
        executeRequest(request);
    }

    public PagedList<FeedStarDto> listStars(long feedId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/%d/star/list", feedId), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), FeedStarDto.class);
    }

    public void star(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feed/%d/star", feedId), builder.build());
        executeRequest(request);
    }

    public void unstar(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feed/%d/unstar", feedId), builder.build());
        executeRequest(request);
    }
}
