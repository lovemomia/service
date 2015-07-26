package cn.momia.service.web.ctrl.feed;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.feed.Feed;
import cn.momia.service.feed.comment.FeedComment;
import cn.momia.service.user.base.User;
import cn.momia.service.web.ctrl.AbstractController;
import cn.momia.service.web.ctrl.dto.PagedListDto;
import cn.momia.service.web.ctrl.feed.dto.FeedCommentDto;
import cn.momia.service.web.ctrl.feed.dto.FeedDto;
import cn.momia.service.web.ctrl.user.dto.MiniUserDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/{id}/star", method = RequestMethod.GET)
    public ResponseMessage getFeedStaredUsers(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return new ResponseMessage(PagedListDto.EMPTY);

        long totalCount = feedServiceFacade.queryStaredUsersCount(id);
        if (totalCount <= 0) return new ResponseMessage(PagedListDto.EMPTY);

        List<Long> userIds = feedServiceFacade.queryStaredUserIds(id, start, count);
        List<User> users = userServiceFacade.getUsers(userIds);

        PagedListDto staredUsersDto = new PagedListDto();
        staredUsersDto.setTotalCount(totalCount);
        if (start + count < totalCount) staredUsersDto.setNextIndex(start + count);

        for (User user : users) {
            staredUsersDto.add(new MiniUserDto(user));
        }

        return new ResponseMessage(staredUsersDto);
    }

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.GET)
    public ResponseMessage getFeedComments(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return new ResponseMessage(PagedListDto.EMPTY);

        long totalCount = feedServiceFacade.queryCommentsCount(id);
        if (totalCount <= 0) return new ResponseMessage(PagedListDto.EMPTY);

        List<FeedComment> comments = feedServiceFacade.queryComments(id, start, count);

        List<Long> userIds = new ArrayList<Long>();
        for (FeedComment comment : comments) userIds.add(comment.getUserId());
        List<User> users = userServiceFacade.getUsers(userIds);
        Map<Long, User> usersMap = new HashMap<Long, User>();
        for (User user : users) usersMap.put(user.getId(), user);

        PagedListDto feedCommentsDto = new PagedListDto();
        feedCommentsDto.setTotalCount(totalCount);
        if (start + count < totalCount) feedCommentsDto.setNextIndex(start + count);

        for (FeedComment comment : comments) {
            User user = usersMap.get(comment.getUserId());
            if (user == null) continue;

            feedCommentsDto.add(new FeedCommentDto(comment, user));
        }

        return new ResponseMessage(feedCommentsDto);
    }
}
