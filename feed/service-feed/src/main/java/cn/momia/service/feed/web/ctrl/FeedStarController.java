package cn.momia.service.feed.web.ctrl;

import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.api.feed.dto.Feed;
import cn.momia.service.feed.base.FeedService;
import cn.momia.service.feed.star.FeedStarService;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/feed")
public class FeedStarController extends BaseController {
    @Autowired private FeedService feedService;
    @Autowired private FeedStarService feedStarService;

    @RequestMapping(value = "/{fid}/star/list", method = RequestMethod.GET)
    public MomiaHttpResponse listStaredUsers(@PathVariable(value = "fid") long feedId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        Feed feed = feedService.get(feedId);
        if (!feed.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        long totalCount = feedStarService.queryUserIdsCount(feedId);
        List<Long> userIds = feedStarService.queryUserIds(feedId, start, count);

        PagedList<Long> pagedStaredUserIds = new PagedList<Long>(totalCount, start, count);
        pagedStaredUserIds.setList(userIds);

        return MomiaHttpResponse.SUCCESS(pagedStaredUserIds);
    }

    @RequestMapping(value = "/{fid}/star", method = RequestMethod.POST)
    public MomiaHttpResponse star(@RequestParam(value = "uid") long userId, @PathVariable(value = "fid") long feedId) {
        Feed feed = feedService.get(feedId);
        if (!feed.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        if (userId <= 0 || !feedStarService.add(userId, feedId)) return MomiaHttpResponse.FAILED("赞失败");

        feedService.increaseStarCount(feedId);
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{fid}/unstar", method = RequestMethod.POST)
    public MomiaHttpResponse unstar(@RequestParam(value = "uid") long userId, @PathVariable(value = "fid") long feedId) {
        if (userId <= 0 || feedId <= 0 || !feedStarService.delete(userId, feedId)) return MomiaHttpResponse.FAILED("取消赞失败");

        feedService.decreaseStarCount(feedId);
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/star", method = RequestMethod.GET)
    public MomiaHttpResponse queryStaredFeedIds(@RequestParam(value = "uid") long userId, @RequestParam String fids) {
        Set<Long> feedIds = new HashSet<Long>();
        for (String feedId : Splitter.on(",").trimResults().omitEmptyStrings().split(fids)) {
            feedIds.add(Long.valueOf(feedId));
        }

        return MomiaHttpResponse.SUCCESS(feedStarService.queryStaredFeedIds(userId, feedIds));
    }
}
