package cn.momia.service.web.ctrl.feed;

import cn.momia.service.user.participant.Participant;
import cn.momia.service.web.response.ResponseMessage;
import cn.momia.service.feed.facade.Feed;
import cn.momia.service.feed.comment.FeedComment;
import cn.momia.service.user.base.User;
import cn.momia.service.web.ctrl.AbstractController;
import cn.momia.service.web.ctrl.dto.PagedListDto;
import cn.momia.service.web.ctrl.feed.dto.FeedCommentDto;
import cn.momia.service.web.ctrl.feed.dto.FeedDto;
import cn.momia.service.web.ctrl.user.dto.MiniUserDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/feed")
public class FeedController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedController.class);

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getFeeds(@RequestParam String utoken, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        long totalCount = feedServiceFacade.queryFollowedCountByUser(user.getId());
        List<Feed> feeds = feedServiceFacade.queryFollowedByUser(user.getId(), start, count);

        return ResponseMessage.SUCCESS(buildFeedsDto(utoken, feeds, totalCount, start, count));
    }

    private PagedListDto buildFeedsDto(String utoken, List<Feed> feeds, long totalCount, @RequestParam int start, @RequestParam int count) {
        Set<Long> staredFeedIds = new HashSet<Long>();
        if (!StringUtils.isBlank(utoken)) {
            User user = userServiceFacade.getUserByToken(utoken);
            if (user.exists()) {
                Set<Long> feedIds = new HashSet<Long>();
                for (Feed feed : feeds) feedIds.add(feed.getId());
                staredFeedIds.addAll(feedServiceFacade.queryStaredFeeds(user.getId(), feedIds));
            }
        }

        Set<Long> userIds = new HashSet<Long>();
        for (Feed feed : feeds) userIds.add(feed.getUserId());
        List<User> users = userServiceFacade.getUsers(userIds);
        Map<Long, User> usersMap = new HashMap<Long, User>();
        for (User user : users) usersMap.put(user.getId(), user);

        Set<Long> childIds = new HashSet<Long>();
        for (User user : users) childIds.addAll(user.getChildren());
        List<Participant> children = userServiceFacade.getParticipants(childIds);
        Map<Long, Participant> childrenMap = new HashMap<Long, Participant>();
        for (Participant child : children) childrenMap.put(child.getId(), child);

        PagedListDto feedsDto = new PagedListDto(totalCount, start, count);
        for (Feed feed : feeds) {
            User user = usersMap.get(feed.getUserId());
            if (user == null) continue;

            List<Participant> userChildren = new ArrayList<Participant>();
            for (long childId : user.getChildren()) {
                Participant child = childrenMap.get(childId);
                if (child != null) userChildren.add(child);
            }

            feedsDto.add(new FeedDto(feed, user, userChildren, staredFeedIds.contains(feed.getId())));
        }
        return feedsDto;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage addFeed(@RequestBody Feed feed) {
        long feedId = feedServiceFacade.addFeed(feed);
        if (feedId <= 0) return ResponseMessage.FAILED("发表Feed失败");
        try {
            // TODO 异步推送
            List<Long> followedIds = userServiceFacade.getFollowedIds(feed.getUserId());
            feedServiceFacade.pushFeed(feedId, followedIds);
        } catch (Exception e) {
            LOGGER.error("fail to push feed: {}", feed.getId());
        }

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getFeed(@RequestParam String utoken, @PathVariable long id) {
        Feed feed = feedServiceFacade.getFeed(id);
        if (!feed.exists()) return ResponseMessage.FAILED("无效的Feed");

        User feedUser = userServiceFacade.getUser(feed.getUserId());
        if (!feedUser.exists()) return ResponseMessage.FAILED("无效的Feed");

        boolean stared = false;
        if (!StringUtils.isBlank(utoken)) {
            User user = userServiceFacade.getUserByToken(utoken);
            if (user.exists()) stared = feedServiceFacade.isStared(user.getId(), id);
        }

        return ResponseMessage.SUCCESS(new FeedDto(feed, feedUser, userServiceFacade.getChildren(feedUser.getId(), feedUser.getChildren()), stared));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage deleteFeed(@RequestParam String utoken, @PathVariable long id) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!feedServiceFacade.deleteFeed(user.getId(), id)) return ResponseMessage.FAILED("删除Feed失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}/star", method = RequestMethod.GET)
    public ResponseMessage getFeedStaredUsers(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        long totalCount = feedServiceFacade.queryStaredUsersCount(id);
        if (totalCount <= 0) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        List<Long> userIds = feedServiceFacade.queryStaredUserIds(id, start, count);
        List<User> users = userServiceFacade.getUsers(userIds);

        PagedListDto staredUsersDto = new PagedListDto(totalCount, start, count);
        for (User user : users) {
            staredUsersDto.add(new MiniUserDto(user));
        }

        return ResponseMessage.SUCCESS(staredUsersDto);
    }

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.GET)
    public ResponseMessage getFeedComments(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        long totalCount = feedServiceFacade.queryCommentsCount(id);
        if (totalCount <= 0) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        List<FeedComment> comments = feedServiceFacade.queryComments(id, start, count);

        List<Long> userIds = new ArrayList<Long>();
        for (FeedComment comment : comments) userIds.add(comment.getUserId());
        List<User> users = userServiceFacade.getUsers(userIds);
        Map<Long, User> usersMap = new HashMap<Long, User>();
        for (User user : users) usersMap.put(user.getId(), user);

        PagedListDto feedCommentsDto = new PagedListDto(totalCount, start, count);
        for (FeedComment comment : comments) {
            User user = usersMap.get(comment.getUserId());
            if (user == null) continue;

            feedCommentsDto.add(new FeedCommentDto(comment, user));
        }

        return ResponseMessage.SUCCESS(feedCommentsDto);
    }

    @RequestMapping(value = "/topic", method = RequestMethod.GET)
    public ResponseMessage feedTopic(@RequestParam String utoken,
                                     @RequestParam(value = "tid") long topicId,
                                     @RequestParam int start,
                                     @RequestParam int count) {
        if (topicId <= 0 || isInvalidLimit(start, count)) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        long totalCount = feedServiceFacade.queryCountByTopic(topicId);
        List<Feed> feeds = feedServiceFacade.queryByTopic(topicId, start, count);

        return ResponseMessage.SUCCESS(buildFeedsDto(utoken, feeds, totalCount, start, count));
    }

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.POST)
    public ResponseMessage addComment(@RequestParam String utoken, @PathVariable long id, @RequestParam String content) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!feedServiceFacade.addComment(user.getId(), id, content)) return ResponseMessage.FAILED("发表评论失败");

        feedServiceFacade.increaseCommentCount(id);

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.DELETE)
    public ResponseMessage deleteComment(@RequestParam String utoken, @PathVariable long id, @RequestParam(value = "cmid") long commentId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!feedServiceFacade.deleteComment(user.getId(), id, commentId)) return ResponseMessage.FAILED("删除评论失败");

        feedServiceFacade.decreaseCommentCount(id);

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}/star", method = RequestMethod.POST)
    public ResponseMessage star(@RequestParam String utoken, @PathVariable long id) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!feedServiceFacade.star(user.getId(), id)) return ResponseMessage.FAILED("赞失败");

        feedServiceFacade.increaseStarCount(id);

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}/unstar", method = RequestMethod.DELETE)
    public ResponseMessage unstar(@RequestParam String utoken, @PathVariable long id) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!feedServiceFacade.unstar(user.getId(), id)) return ResponseMessage.FAILED("取消赞失败");

        feedServiceFacade.decreaseStarCount(id);

        return ResponseMessage.SUCCESS;
    }
}
