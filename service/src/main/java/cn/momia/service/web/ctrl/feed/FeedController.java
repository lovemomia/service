package cn.momia.service.web.ctrl.feed;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.feed.Feed;
import cn.momia.service.user.base.User;
import cn.momia.service.web.ctrl.AbstractController;
import cn.momia.service.web.ctrl.feed.dto.FeedDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
public class FeedController extends AbstractController {
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getFeed(@PathVariable long id) {
        Feed feed = feedServiceFacade.get(id);
        if (!feed.exists()) return ResponseMessage.FAILED("无效的Feed");

        User user = userServiceFacade.getUser(feed.getUserId());
        if (!user.exists()) return ResponseMessage.FAILED("无效的Feed");

        return new ResponseMessage(new FeedDto(feed, user, userServiceFacade.getChildren(user.getId(), user.getChildren())));
    }
}
