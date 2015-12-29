package cn.momia.api.feed;

import cn.momia.api.feed.dto.Feed;
import cn.momia.api.feed.dto.FeedComment;
import cn.momia.api.feed.dto.FeedTag;
import cn.momia.common.core.HttpServiceApi;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Collection;
import java.util.List;

public class FeedServiceApi extends HttpServiceApi {
    public void follow(long userId, long followedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("fuid", followedId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feed/follow"), builder.build());
        execute(request);
    }

    public PagedList<Feed> list(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed"), builder.build());

        return executeReturnPagedList(request, Feed.class);
    }

    public PagedList<Feed> listFeedsOfUser(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/user"), builder.build());

        return executeReturnPagedList(request, Feed.class);
    }

    public PagedList<Feed> queryBySubject(long subjectId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("suid", subjectId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/subject"), builder.build());

        return executeReturnPagedList(request, Feed.class);
    }

    public PagedList<Feed> queryByCourse(long courseId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("coid", courseId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/course"), builder.build());

        return executeReturnPagedList(request, Feed.class);
    }

    public boolean isOfficialUser(long userId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/official"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public List<FeedTag> listRecommendedTags(int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/tag/recommend"), builder.build());

        return executeReturnList(request, FeedTag.class);
    }

    public List<FeedTag> listHotTags(int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/tag/hot"), builder.build());

        return executeReturnList(request, FeedTag.class);
    }

    public void add(JSONObject feedJson) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feed"), feedJson.toString());
        execute(request);
    }

    public Feed get(long feedId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/%d", feedId));
        return executeReturnObject(request, Feed.class);
    }

    public boolean delete(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/feed/%d", feedId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public PagedList<FeedComment> listComments(long feedId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/%d/comment/list", feedId), builder.build());

        return executeReturnPagedList(request, FeedComment.class);
    }

    public void addComment(long userId, long feedId, String content) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("content", content);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feed/%d/comment", feedId), builder.build());
        execute(request);
    }

    public void deleteComment(long userId, long feedId, long commentId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/feed/%d/comment/%d", feedId, commentId), builder.build());
        execute(request);
    }

    public PagedList<Long> listStaredUserIds(long feedId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/%d/star/list", feedId), builder.build());

        return executeReturnPagedList(request, Long.class);
    }

    public void star(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feed/%d/star", feedId), builder.build());
        execute(request);
    }

    public void unstar(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feed/%d/unstar", feedId), builder.build());
        execute(request);
    }

    public List<Long> queryStaredFeedIds(long userId, Collection<Long> feedIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("fids", StringUtils.join(feedIds, ","));
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/feed/star"), builder.build());

        return executeReturnList(request, Long.class);
    }
}
