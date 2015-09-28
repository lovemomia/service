package cn.momia.service.feed.web.ctrl;

import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.feed.facade.Feed;
import cn.momia.service.feed.facade.FeedServiceFacade;
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
    @Autowired private FeedServiceFacade feedServiceFacade;

    @RequestMapping(value = "/{id}/star/list", method = RequestMethod.GET)
    public MomiaHttpResponse listStaredUsers(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        Feed feed = feedServiceFacade.getFeed(id);
        if (!feed.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        long totalCount = feedServiceFacade.queryStaredUsersCount(id);
        if (totalCount <= 0) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        List<Long> userIds = feedServiceFacade.queryStaredUserIds(id, start, count);
        List<UserDto> users = UserServiceApi.USER.list(userIds, UserDto.Type.MINI);

        PagedList<UserDto> pagedStaredUserDtos = new PagedList(totalCount, start, count);
        List<UserDto> staredUserDtos = new ArrayList<UserDto>();
        for (UserDto user : users) {
            staredUserDtos.add(user);
        }
        pagedStaredUserDtos.setList(staredUserDtos);

        return MomiaHttpResponse.SUCCESS(pagedStaredUserDtos);
    }

    @RequestMapping(value = "/{id}/star", method = RequestMethod.POST)
    public MomiaHttpResponse star(@RequestParam(value = "uid") long userId, @PathVariable long id) {
        Feed feed = feedServiceFacade.getFeed(id);
        if (!feed.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        if (userId <= 0 || !feedServiceFacade.star(userId, id)) return MomiaHttpResponse.FAILED("赞失败");

        feedServiceFacade.increaseStarCount(id);
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{id}/unstar", method = RequestMethod.POST)
    public MomiaHttpResponse unstar(@RequestParam(value = "uid") long userId, @PathVariable long id) {
        if (userId <= 0 || id <= 0 || !feedServiceFacade.unstar(userId, id)) return MomiaHttpResponse.FAILED("取消赞失败");

        feedServiceFacade.decreaseStarCount(id);
        return MomiaHttpResponse.SUCCESS;
    }
}
