package cn.momia.service.feed.web.ctrl;

import cn.momia.api.feed.dto.FeedDto;
import cn.momia.api.user.dto.ChildDto;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.api.dto.PagedList;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.UserDto;
import cn.momia.service.feed.base.Feed;
import cn.momia.service.feed.base.FeedService;
import cn.momia.service.feed.star.FeedStarService;
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

    @Autowired private FeedService feedService;
    @Autowired private FeedStarService feedStarService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    public MomiaHttpResponse follow(@RequestParam(value = "uid") long userId, @RequestParam(value = "fuid") long followedId) {
        if (!feedService.isFollowed(userId, followedId) && !feedService.follow(userId, followedId)) return MomiaHttpResponse.FAILED("关注失败");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam(value = "uid") long userId,
                                  @RequestParam int start,
                                  @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = userId > 0 ? feedService.queryFollowedCountByUser(userId) : feedService.queryOfficialFeedsCount();
        List<Feed> feeds = userId > 0 ? feedService.queryFollowedByUser(userId, start, count) : feedService.queryOfficialFeeds(start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedFeedDtos(userId, feeds, totalCount, start, count));
    }

    private PagedList<FeedDto> buildPagedFeedDtos(long userId, List<Feed> feeds, long totalCount, int start, int count) {
        Set<Long> staredFeedIds = new HashSet<Long>();
        if (userId > 0) {
            Set<Long> feedIds = new HashSet<Long>();
            for (Feed feed : feeds) {
                feedIds.add(feed.getId());
            }
            staredFeedIds.addAll(feedStarService.queryStaredFeeds(userId, feedIds));
        }

        Set<Long> userIds = new HashSet<Long>();
        for (Feed feed : feeds) {
            userIds.add(feed.getUserId());
        }

        List<UserDto> users = userServiceApi.list(userIds, UserDto.Type.FULL);
        Map<Long, UserDto> usersMap = new HashMap<Long, UserDto>();
        for (UserDto user : users) {
            usersMap.put(user.getId(), user);
        }

        List<FeedDto> feedDtos = new ArrayList<FeedDto>();
        for (Feed feed : feeds) {
            UserDto user = usersMap.get(feed.getUserId());
            if (user == null) continue;

            feedDtos.add(buildFeedDto(feed, user, staredFeedIds.contains(feed.getId())));
        }

        PagedList<FeedDto> pagedFeedDtos = new PagedList(totalCount, start, count);
        pagedFeedDtos.setList(feedDtos);

        return pagedFeedDtos;
    }

    private FeedDto buildFeedDto(Feed feed, UserDto user, boolean stared) {
        FeedDto feedDto = new FeedDto();
        feedDto.setId(feed.getId());
        feedDto.setType(feed.getType());
        feedDto.setContent(feed.getContent());
        feedDto.setImgs(feed.getImgs());

        feedDto.setCourseId(feed.getCourseId());
        feedDto.setCourseTitle(feed.getCourseTitle());

        feedDto.setAddTime(feed.getAddTime());
        feedDto.setPoi(feed.getLng() + ":" + feed.getLat());
        feedDto.setCommentCount(feed.getCommentCount());
        feedDto.setStarCount(feed.getStarCount());
        feedDto.setOfficial(feed.getOfficial() > 0);
        feedDto.setUserId(user.getId());
        feedDto.setAvatar(user.getAvatar());
        feedDto.setNickName(user.getNickName());
        feedDto.setChildren(formatChildren(user.getChildren()));
        feedDto.setStared(stared);

        return feedDto;
    }

    private List<String> formatChildren(List<ChildDto> children) {
        List<String> formatedChildren = new ArrayList<String>();
        for (int i = 0; i < Math.min(2, children.size()); i++) {
            ChildDto child = children.get(i);
            formatedChildren.add(child.getSex() + "孩" + TimeUtil.formatAge(child.getBirthday()));
        }

        return formatedChildren;
    }

    @RequestMapping(value = "/course", method = RequestMethod.GET)
    public MomiaHttpResponse queryByCourse(@RequestParam(value = "uid") long userId,
                                           @RequestParam(value = "coid") long courseId,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = feedService.queryCountByCourse(courseId);
        List<Feed> feeds = feedService.queryByCourse(courseId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedFeedDtos(userId, feeds, totalCount, start, count));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse add(@RequestBody Feed feed) {
        long feedId = feedService.add(feed);
        if (feedId <= 0) return MomiaHttpResponse.FAILED("发表Feed失败");

        try {
            // TODO 异步推送
            List<Long> followedIds = feedService.getFollowedIds(feed.getUserId());
            followedIds.add(feed.getUserId());
            feedService.push(feedId, followedIds);
        } catch (Exception e) {
            LOGGER.error("fail to push feed: {}", feed.getId());
        }

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{fid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam(value = "uid", defaultValue = "0") long userId, @PathVariable(value = "fid") long feedId) {
        Feed feed = feedService.get(feedId);
        if (!feed.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        UserDto feedUser = userServiceApi.get(feed.getUserId());
        if (!feedUser.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        boolean stared = userId > 0 && feedStarService.isStared(userId, feedId);

        return MomiaHttpResponse.SUCCESS(buildFeedDto(feed, feedUser, stared));
    }

    @RequestMapping(value = "/{fid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse delete(@RequestParam(value = "uid") long userId, @PathVariable(value = "fid") long feedId) {
        if (userId <= 0 || feedId <= 0) return MomiaHttpResponse.FAILED("无效的Feed");
        return MomiaHttpResponse.SUCCESS(feedService.delete(userId, feedId));
    }

    @RequestMapping(value = "/live", method = RequestMethod.GET)
    public MomiaHttpResponse live(@RequestParam(value = "suid") long subjectId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = feedService.queryLiveCountBySubject(subjectId);
        List<Feed> feeds = feedService.queryLiveBySubject(subjectId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedFeedDtos(0, feeds, totalCount, start, count));
    }

    @RequestMapping(value = "/homework", method = RequestMethod.GET)
    public MomiaHttpResponse homework(@RequestParam(value = "coid") long courseId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = feedService.queryHomeworkCountByCourse(courseId);
        List<Feed> feeds = feedService.queryHomeworkByCourse(courseId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedFeedDtos(0, feeds, totalCount, start, count));
    }
}
