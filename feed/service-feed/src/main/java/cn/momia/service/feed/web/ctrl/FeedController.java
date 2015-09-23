package cn.momia.service.feed.web.ctrl;

import cn.momia.api.feed.dto.FeedDto;
import cn.momia.api.user.dto.ParticipantDto;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.SexUtil;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.api.dto.PagedList;
import cn.momia.service.feed.facade.Feed;
import cn.momia.service.feed.facade.FeedImage;
import cn.momia.service.feed.facade.FeedServiceFacade;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FeedController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedController.class);

    @Autowired private FeedServiceFacade feedServiceFacade;

    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    public MomiaHttpResponse follow(@RequestParam(value = "uid") long userId, @RequestParam(value = "fuid") long followedId) {
        if (!feedServiceFacade.follow(userId, followedId)) return MomiaHttpResponse.FAILED("关注失败");

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount;
        List<Feed> feeds;
        if (userId > 0) {
            totalCount = feedServiceFacade.queryFollowedCountByUser(userId);
            feeds = feedServiceFacade.queryFollowedByUser(userId, start, count);
        } else {
            totalCount = feedServiceFacade.queryPublicFeedsCount();
            feeds = feedServiceFacade.queryPublicFeeds(start, count);
        }

        return MomiaHttpResponse.SUCCESS(buildPagedFeedDtos(userId, feeds, totalCount, start, count));
    }

    private PagedList buildPagedFeedDtos(long userId, List<Feed> feeds, long totalCount, @RequestParam int start, @RequestParam int count) {
        Set<Long> staredFeedIds = new HashSet<Long>();
        if (userId > 0) {
            Set<Long> feedIds = new HashSet<Long>();
            for (Feed feed : feeds) feedIds.add(feed.getId());
            staredFeedIds.addAll(feedServiceFacade.queryStaredFeeds(userId, feedIds));
        }

        Set<Long> userIds = new HashSet<Long>();
        for (Feed feed : feeds) userIds.add(feed.getUserId());
        List<UserDto> users = UserServiceApi.USER.list(userIds, UserDto.Type.FULL);
        Map<Long, UserDto> usersMap = new HashMap<Long, UserDto>();
        for (UserDto user : users) usersMap.put(user.getId(), user);

        PagedList pagedFeedDtos = new PagedList(totalCount, start, count);
        List<FeedDto> feedDtos = new ArrayList<FeedDto>();
        for (Feed feed : feeds) {
            UserDto user = usersMap.get(feed.getUserId());
            if (user == null) continue;

            feedDtos.add(buildFeedDto(feed, user, staredFeedIds.contains(feed.getId())));
        }
        pagedFeedDtos.setList(feedDtos);

        return pagedFeedDtos;
    }

    private FeedDto buildFeedDto(Feed feed, UserDto user, boolean stared) {
        FeedDto feedDto = new FeedDto();
        feedDto.setId(feed.getId());
        feedDto.setType(feed.getType());
        feedDto.setTopicId(feed.getTopicId());
        feedDto.setTopicProductId(feed.getTopicProductId());
        feedDto.setTopic(feed.getTopic());
        feedDto.setImgs(getImgs(feed));
        feedDto.setContent(feed.getContent());
        feedDto.setAddTime(feed.getAddTime());
        feedDto.setPoi(feed.getPoi());
        feedDto.setCommentCount(feed.getCommentCount());
        feedDto.setStarCount(feed.getStarCount());
        feedDto.setUserId(user.getId());
        feedDto.setAvatar(user.getAvatar());
        feedDto.setNickName(user.getNickName());
        feedDto.setChildren(getChildren(user));
        feedDto.setStared(stared);

        return feedDto;
    }

    private List<String> getImgs(Feed feed) {
        List<String> imgs = new ArrayList<String>();
        for (FeedImage feedImage : feed.getImgs()) imgs.add(feedImage.getUrl());

        return imgs;
    }

    public List<String> getChildren(UserDto user) {
        List<String> children = new ArrayList<String>();

        if (user.getChildren() != null) {
            int count = 0;
            for (ParticipantDto child : user.getChildren()) {
                if (TimeUtil.isAdult(child.getBirthday())) continue;

                String ageStr = TimeUtil.formatAge(child.getBirthday());
                if (SexUtil.isInvalid(child.getSex())) children.add("孩子" + ageStr);
                else children.add(child.getSex() + "孩" + ageStr);

                count++;
                if (count >= 2) break;
            }
        }

        return children;
    }

    @RequestMapping(value = "/topic", method = RequestMethod.GET)
    public MomiaHttpResponse topic(@RequestParam(value = "uid") long userId,
                                   @RequestParam(value = "tid") long topicId,
                                   @RequestParam int start,
                                   @RequestParam int count) {
        if (topicId <= 0 || isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = feedServiceFacade.queryCountByTopic(topicId);
        List<Feed> feeds = feedServiceFacade.queryByTopic(topicId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedFeedDtos(userId, feeds, totalCount, start, count));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse add(@RequestBody Feed feed) {
        long feedId = feedServiceFacade.addFeed(feed);
        if (feedId <= 0) return MomiaHttpResponse.FAILED("发表Feed失败");
        try {
            // TODO 异步推送
            List<Long> followedIds = feedServiceFacade.queryFollowedIds(feed.getUserId());
            followedIds.add(feed.getUserId());
            feedServiceFacade.pushFeed(feedId, followedIds);
        } catch (Exception e) {
            LOGGER.error("fail to push feed: {}", feed.getId());
        }

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam(value = "uid", defaultValue = "0") long userId, @PathVariable long id) {
        Feed feed = feedServiceFacade.getFeed(id);
        if (!feed.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        UserDto feedUser = UserServiceApi.USER.get(feed.getUserId());
        if (feedUser.getId() <= 0) return MomiaHttpResponse.FAILED("无效的Feed");

        boolean stared = feedServiceFacade.isStared(userId, id);

        return MomiaHttpResponse.SUCCESS(buildFeedDto(feed, feedUser, stared));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public MomiaHttpResponse delete(@RequestParam(value = "uid") long userId, @PathVariable long id) {
        if (!feedServiceFacade.deleteFeed(userId, id)) return MomiaHttpResponse.FAILED("删除Feed失败");
        return MomiaHttpResponse.SUCCESS;
    }
}
