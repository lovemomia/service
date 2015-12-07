package cn.momia.service.feed.web.ctrl;

import cn.momia.api.feed.dto.UserFeed;
import cn.momia.api.feed.dto.FeedTag;
import cn.momia.api.user.dto.Child;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.api.dto.PagedList;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.service.feed.base.Feed;
import cn.momia.service.feed.base.FeedService;
import cn.momia.service.feed.star.FeedStarService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
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

        return MomiaHttpResponse.SUCCESS(buildPagedUserFeeds(userId, feeds, totalCount, start, count));
    }

    private PagedList<UserFeed> buildPagedUserFeeds(long userId, List<Feed> feeds, long totalCount, int start, int count) {
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

        List<User> users = userServiceApi.list(userIds, User.Type.FULL);
        Map<Long, User> usersMap = new HashMap<Long, User>();
        for (User user : users) {
            usersMap.put(user.getId(), user);
        }

        List<UserFeed> userFeeds = new ArrayList<UserFeed>();
        for (Feed feed : feeds) {
            User user = usersMap.get(feed.getUserId());
            if (user == null) continue;

            userFeeds.add(buildUserFeed(feed, user, staredFeedIds.contains(feed.getId())));
        }

        PagedList<UserFeed> pagedFeedDtos = new PagedList(totalCount, start, count);
        pagedFeedDtos.setList(userFeeds);

        return pagedFeedDtos;
    }

    private UserFeed buildUserFeed(Feed feed, User user, boolean stared) {
        UserFeed userFeed = new UserFeed();
        userFeed.setId(feed.getId());
        userFeed.setType(feed.getType());
        userFeed.setContent(feed.getContent());
        userFeed.setImgs(feed.getImgs());

        userFeed.setTagId(feed.getTagId());
        userFeed.setTagName(feed.getTagName());
        userFeed.setCourseId(feed.getCourseId());
        userFeed.setCourseTitle(feed.getCourseTitle());

        userFeed.setAddTime(TimeUtil.formatAddTime(feed.getAddTime()));
        userFeed.setPoi(feed.getLng() + ":" + feed.getLat());
        userFeed.setCommentCount(feed.getCommentCount());
        userFeed.setStarCount(feed.getStarCount());
        userFeed.setOfficial(feed.getOfficial() > 0);
        userFeed.setUserId(user.getId());
        userFeed.setAvatar(user.getAvatar());
        userFeed.setNickName(user.getNickName());
        userFeed.setChildren(formatChildren(user.getChildren()));
        userFeed.setStared(stared);

        return userFeed;
    }

    private List<String> formatChildren(List<Child> children) {
        List<String> formatedChildren = new ArrayList<String>();
        for (int i = 0; i < Math.min(2, children.size()); i++) {
            Child child = children.get(i);
            formatedChildren.add(child.getSex() + "孩" + TimeUtil.formatAge(child.getBirthday()));
        }

        return formatedChildren;
    }
    
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public MomiaHttpResponse listFeedsOfUser(@RequestParam(value = "uid") long userId,
                                             @RequestParam int start,
                                             @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount =feedService.queryCountByUser(userId);
        List<Feed> feeds = feedService.queryByUser(userId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedUserFeeds(userId, feeds, totalCount, start, count));
    }

    @RequestMapping(value = "/subject", method = RequestMethod.GET)
    public MomiaHttpResponse queryBySubject(@RequestParam(value = "uid") long userId,
                                            @RequestParam(value = "suid") long subjectId,
                                            @RequestParam int start,
                                            @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = feedService.queryCountBySubject(subjectId);
        List<Feed> feeds = feedService.queryBySubject(subjectId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedUserFeeds(userId, feeds, totalCount, start, count));
    }

    @RequestMapping(value = "/course", method = RequestMethod.GET)
    public MomiaHttpResponse queryByCourse(@RequestParam(value = "uid") long userId,
                                           @RequestParam(value = "coid") long courseId,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = feedService.queryCountByCourse(courseId);
        List<Feed> feeds = feedService.queryByCourse(courseId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedUserFeeds(userId, feeds, totalCount, start, count));
    }

    @RequestMapping(value = "/official", method = RequestMethod.GET)
    public MomiaHttpResponse isOfficialUser(@RequestParam(value = "uid") long userId) {
        return MomiaHttpResponse.SUCCESS(feedService.isOfficialUser(userId));
    }

    @RequestMapping(value = "/tag/recommend", method = RequestMethod.GET)
    public MomiaHttpResponse listRecommendedTags(@RequestParam int count) {
        return MomiaHttpResponse.SUCCESS(feedService.listRecommendedTags(count));
    }

    @RequestMapping(value = "/tag/hot", method = RequestMethod.GET)
    public MomiaHttpResponse listHotTags(@RequestParam int count) {
        return MomiaHttpResponse.SUCCESS(feedService.listHotTags(count));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse add(@RequestBody Feed feed) {
        if (feed.getImgs() != null && feed.getImgs().size() > 9) return MomiaHttpResponse.FAILED("上传的图片过多，1次最多上传9张图片");

        long tagId = feed.getTagId();
        String tagName = feed.getTagName();
        if (tagId <= 0 && !StringUtils.isBlank(tagName)) {
            tagId = addTag(feed.getUserId(), tagName);
            feed.setTagId(tagId);
        }

        boolean isOfficialUser = feedService.isOfficialUser(feed.getUserId());
        if (isOfficialUser) feed.setOfficial(1);

        long feedId = feedService.add(feed);
        if (feedId <= 0) return MomiaHttpResponse.FAILED("发表Feed失败");

        try {
            // TODO 异步推送
            List<Long> followedIds = isOfficialUser ? Lists.newArrayList(0L) : feedService.getFollowedIds(feed.getUserId());
            if (!isOfficialUser) followedIds.add(feed.getUserId());
            feedService.push(feedId, followedIds);
        } catch (Exception e) {
            LOGGER.error("fail to push feed: {}", feed.getId());
        }

        return MomiaHttpResponse.SUCCESS;
    }

    private long addTag(long userId, String tagName) {
        FeedTag feedTag = feedService.query(tagName);
        if (feedTag.exists()) return feedTag.getId();

        long tagId = feedService.addTag(userId, tagName);
        return tagId > 0 ? tagId : 0;
    }

    @RequestMapping(value = "/{fid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam(value = "uid", defaultValue = "0") long userId, @PathVariable(value = "fid") long feedId) {
        Feed feed = feedService.get(feedId);
        if (!feed.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        User feedUser = userServiceApi.get(feed.getUserId());
        if (!feedUser.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        boolean stared = userId > 0 && feedStarService.isStared(userId, feedId);

        return MomiaHttpResponse.SUCCESS(buildUserFeed(feed, feedUser, stared));
    }

    @RequestMapping(value = "/{fid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse delete(@RequestParam(value = "uid") long userId, @PathVariable(value = "fid") long feedId) {
        if (userId <= 0 || feedId <= 0) return MomiaHttpResponse.FAILED("无效的Feed");
        return MomiaHttpResponse.SUCCESS(feedService.delete(userId, feedId));
    }
}
