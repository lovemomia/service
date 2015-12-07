package cn.momia.service.feed.web.ctrl;

import cn.momia.api.feed.dto.UserComment;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.feed.base.Feed;
import cn.momia.service.feed.base.FeedService;
import cn.momia.service.feed.comment.FeedComment;
import cn.momia.service.feed.comment.FeedCommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FeedCommentController extends BaseController {
    @Autowired private FeedService feedService;
    @Autowired private FeedCommentService feedCommentService;

    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/{fid}/comment/list", method = RequestMethod.GET)
    public MomiaHttpResponse listComments(@PathVariable(value = "fid") long feedId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        Feed feed = feedService.get(feedId);
        if (!feed.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        long totalCount = feedCommentService.queryCount(feedId);
        if (totalCount <= 0) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        List<FeedComment> comments = feedCommentService.query(feedId, start, count);

        List<Long> userIds = new ArrayList<Long>();
        for (FeedComment comment : comments) {
            userIds.add(comment.getUserId());
        }
        List<User> users = userServiceApi.list(userIds, User.Type.MINI);
        Map<Long, User> usersMap = new HashMap<Long, User>();
        for (User user : users) {
            usersMap.put(user.getId(), user);
        }

        PagedList<UserComment> pagedUserComments = new PagedList(totalCount, start, count);
        List<UserComment> userComments = new ArrayList<UserComment>();
        for (FeedComment comment : comments) {
            User user = usersMap.get(comment.getUserId());
            if (user == null) continue;

            userComments.add(buildUserComment(comment, user));
        }
        pagedUserComments.setList(userComments);

        return MomiaHttpResponse.SUCCESS(pagedUserComments);
    }

    private UserComment buildUserComment(FeedComment comment, User user) {
        UserComment userComment = new UserComment();
        userComment.setId(comment.getId());
        userComment.setContent(comment.getContent());
        userComment.setAddTime(TimeUtil.formatAddTime(comment.getAddTime()));
        userComment.setNickName(user.getNickName());
        userComment.setAvatar(user.getAvatar());

        return userComment;
    }

    @RequestMapping(value = "/{fid}/comment", method = RequestMethod.POST)
    public MomiaHttpResponse addComment(@RequestParam(value = "uid") long userId, @PathVariable(value = "fid") long feedId, @RequestParam String content) {
        Feed feed = feedService.get(feedId);
        if (!feed.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        if (userId <= 0 || StringUtils.isBlank(content) ||
                !feedCommentService.add(userId, feedId, content)) return MomiaHttpResponse.FAILED("发表评论失败");

        feedService.increaseCommentCount(feedId);
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{fid}/comment/{cmid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse deleteComment(@RequestParam(value = "uid") long userId, @PathVariable(value = "fid") long feedId, @PathVariable(value = "cmid") long commentId) {
        if (userId <= 0 || feedId <= 0 || commentId <= 0 ||
                !feedCommentService.delete(userId, feedId, commentId)) return MomiaHttpResponse.FAILED("删除评论失败");

        feedService.decreaseCommentCount(feedId);
        return MomiaHttpResponse.SUCCESS;
    }
}
