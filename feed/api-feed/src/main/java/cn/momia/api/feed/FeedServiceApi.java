package cn.momia.api.feed;

import cn.momia.api.feed.dto.Feed;
import cn.momia.api.feed.dto.FeedComment;
import cn.momia.api.feed.dto.FeedTag;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;

public class FeedServiceApi extends HttpServiceApi {
    public boolean follow(long userId, long followedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("fuid", followedId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/feed/follow"), builder.build()), Boolean.class);
    }

    public PagedList<Feed> list(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/feed"), builder.build()), Feed.class);
    }

    public PagedList<Feed> listFeedsOfUser(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/feed/user"), builder.build()), Feed.class);
    }

    public PagedList<Feed> queryBySubject(long subjectId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("suid", subjectId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/feed/subject"), builder.build()), Feed.class);
    }

    public PagedList<Feed> queryByCourse(long courseId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("coid", courseId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/feed/course"), builder.build()), Feed.class);
    }

    public boolean isOfficialUser(long userId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/feed/official"), builder.build()), Boolean.class);
    }

    public List<FeedTag> listRecommendedTags(int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("count", count);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/feed/tag/recommend"), builder.build()), FeedTag.class);
    }

    public List<FeedTag> listHotTags(int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("count", count);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/feed/tag/hot"), builder.build()), FeedTag.class);
    }

    public void add(JSONObject feedJson) {
        execute(MomiaHttpRequestBuilder.POST(url("/feed"), feedJson.toString()));
    }

    public Feed get(long feedId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/feed/%d", feedId)), Feed.class);
    }

    public boolean delete(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        return executeReturnObject(MomiaHttpRequestBuilder.DELETE(url("/feed/%d", feedId), builder.build()), Boolean.class);
    }

    public PagedList<FeedComment> listComments(long feedId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/feed/%d/comment/list", feedId), builder.build()), FeedComment.class);
    }

    public void addComment(long userId, long feedId, String content) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("content", content);
        execute(MomiaHttpRequestBuilder.POST(url("/feed/%d/comment", feedId), builder.build()));
    }

    public void deleteComment(long userId, long feedId, long commentId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        execute(MomiaHttpRequestBuilder.DELETE(url("/feed/%d/comment/%d", feedId, commentId), builder.build()));
    }

    public PagedList<Long> listStaredUserIds(long feedId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/feed/%d/star/list", feedId), builder.build()), Long.class);
    }

    public void star(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        execute(MomiaHttpRequestBuilder.POST(url("/feed/%d/star", feedId), builder.build()));
    }

    public void unstar(long userId, long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        execute(MomiaHttpRequestBuilder.POST(url("/feed/%d/unstar", feedId), builder.build()));
    }

    public List<Long> filterNotStaredFeedIds(long userId, Collection<Long> feedIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("fids", StringUtils.join(feedIds, ","));
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/feed/filter/notstared"), builder.build()), Long.class);
    }
}
