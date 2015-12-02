package cn.momia.service.feed.web.ctrl;

import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.feed.base.Feed;
import cn.momia.service.feed.base.FeedService;
import cn.momia.service.feed.star.FeedStarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedStarController extends BaseController {
    @Autowired private FeedService feedService;
    @Autowired private FeedStarService feedStarService;

    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/{fid}/star/list", method = RequestMethod.GET)
    public MomiaHttpResponse listStaredUsers(@PathVariable(value = "fid") long feedId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        Feed feed = feedService.get(feedId);
        if (!feed.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        long totalCount = feedStarService.queryUserCount(feedId);
        if (totalCount <= 0) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        List<Long> userIds = feedStarService.queryUserIds(feedId, start, count);
        List<User> users = userServiceApi.list(userIds, User.Type.MINI);

        PagedList<User> pagedStaredUserDtos = new PagedList(totalCount, start, count);
        List<User> staredUserDtos = new ArrayList<User>();
        for (User user : users) {
            staredUserDtos.add(user);
        }
        pagedStaredUserDtos.setList(staredUserDtos);

        return MomiaHttpResponse.SUCCESS(pagedStaredUserDtos);
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
}
