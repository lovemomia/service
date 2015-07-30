package cn.momia.mapi.api.v1;

import cn.momia.common.service.config.Configuration;
import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/feed")
public class FeedV1Api extends AbstractV1Api {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getFeeds(@RequestParam String utoken, @RequestParam int start) {
        if (StringUtils.isBlank(utoken) || start < 0) return ResponseMessage.BAD_REQUEST;


        return null;
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResponseMessage feedDetail(@RequestParam(value = "fid") long feedId, @RequestParam(value = "pid") long productId) {
        if (feedId <= 0 || productId <= 0) return ResponseMessage.BAD_REQUEST;

        List<MomiaHttpRequest> requests = buildFeedDetailRequests(feedId, productId);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Object>() {
            @Override
            public Object apply(MomiaHttpResponseCollector collector) {
                JSONObject feedDetailJson = new JSONObject();
                feedDetailJson.put("feed", feedFunc.apply(collector.getResponse("feed")));
                feedDetailJson.put("product", productFunc.apply(collector.getResponse("product")));
                feedDetailJson.put("staredUsers", pagedUsersFunc.apply(collector.getResponse("star")));
                feedDetailJson.put("comments", pagedFeedCommentsFunc.apply(collector.getResponse("comments")));

                return feedDetailJson;
            }
        });
    }

    private List<MomiaHttpRequest> buildFeedDetailRequests(long feedId, long productId) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        requests.add(buildFeedRequest(feedId));
        requests.add(buildProductRequest(productId));
        requests.add(buildStaredUsersRequest(feedId));
        requests.add(buildFeedCommentsRequests(feedId));

        return requests;
    }

    private MomiaHttpRequest buildFeedRequest(long feedId) {
        return MomiaHttpRequest.GET("feed", true, url("feed", feedId));
    }

    private MomiaHttpRequest buildProductRequest(long productId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("full", false);

        return MomiaHttpRequest.GET("product", true, url("product", productId), builder.build());
    }

    private MomiaHttpRequest buildStaredUsersRequest(long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", Configuration.getInt("Feed.Detail.Star.PageSize"));

        return MomiaHttpRequest.GET("star", true, url("feed", feedId, "star"), builder.build());
    }

    private MomiaHttpRequest buildFeedCommentsRequests(long feedId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", Configuration.getInt("Feed.Detail.Comment.PageSize"));

        return MomiaHttpRequest.GET("comments", true, url("feed", feedId, "comment"), builder.build());
    }

    @RequestMapping(value = "/topic", method = RequestMethod.GET)
    public ResponseMessage feedTopic(@RequestParam(value = "pid") long productId, @RequestParam(value = "tid") long topicId, @RequestParam final int start) {
        if (topicId <= 0 || productId <= 0 || start < 0) return ResponseMessage.BAD_REQUEST;

        List<MomiaHttpRequest> requests = buildFeedTopicRequests(productId, topicId, start);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Object>() {
            @Override
            public Object apply(MomiaHttpResponseCollector collector) {
                JSONObject feedTopicJson = new JSONObject();
                if (start == 0) feedTopicJson.put("product", productFunc.apply(collector.getResponse("product")));
                feedTopicJson.put("feeds", pagedFeedsFunc.apply(collector.getResponse("feeds")));

                return feedTopicJson;
            }
        });
    }

    private List<MomiaHttpRequest> buildFeedTopicRequests(long productId, long topicId, int start) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        if (start == 0) requests.add(buildProductRequest(productId));
        requests.add(buildFeedsRequest(topicId, start));

        return requests;
    }

    private MomiaHttpRequest buildFeedsRequest(long topicId, int start) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("tid", topicId)
                .add("start", start)
                .add("count", Configuration.getInt("Feed.PageSize"));

        return MomiaHttpRequest.GET("feeds", true, url("feed/topic"), builder.build());
    }
}

