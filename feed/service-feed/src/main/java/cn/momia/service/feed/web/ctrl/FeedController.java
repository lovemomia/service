package cn.momia.service.feed.web.ctrl;

import cn.momia.api.feed.dto.FeedDto;
import cn.momia.api.feed.dto.FeedTopicDto;
import cn.momia.api.product.ProductServiceApi;
import cn.momia.api.product.dto.ProductDto;
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
import cn.momia.service.feed.topic.FeedTopic;
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

    @Autowired private FeedServiceFacade feedServiceFacade;

    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    public MomiaHttpResponse follow(@RequestParam(value = "uid") long userId, @RequestParam(value = "fuid") long followedId) {
        if (!feedServiceFacade.follow(userId, followedId)) return MomiaHttpResponse.FAILED("关注失败");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam(value = "uid") long userId,
                                  @RequestParam(value = "tid", required = false, defaultValue = "0") long topicId,
                                  @RequestParam int start,
                                  @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = topicId > 0 ? feedServiceFacade.queryCountByTopic(topicId) : feedServiceFacade.queryFollowedCountByUser(userId);
        List<Feed> feeds = topicId > 0 ? feedServiceFacade.queryByTopic(topicId, start, count) : feedServiceFacade.queryFollowedByUser(userId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedFeedDtos(userId, totalCount, feeds, start, count));
    }

    private PagedList<FeedDto> buildPagedFeedDtos(long userId, long totalCount, List<Feed> feeds, int start, int count) {
        Set<Long> staredFeedIds = new HashSet<Long>();
        if (userId > 0) {
            Set<Long> feedIds = new HashSet<Long>();
            for (Feed feed : feeds) feedIds.add(feed.getId());
            staredFeedIds.addAll(feedServiceFacade.queryStaredFeeds(userId, feedIds));
        }

        Set<Long> userIds = new HashSet<Long>();
        Set<Long> topicIds = new HashSet<Long>();
        for (Feed feed : feeds) {
            userIds.add(feed.getUserId());
            topicIds.add(feed.getTopicId());
        }

        List<UserDto> users = UserServiceApi.USER.list(userIds, UserDto.Type.FULL);
        Map<Long, UserDto> usersMap = new HashMap<Long, UserDto>();
        for (UserDto user : users) usersMap.put(user.getId(), user);

        List<FeedTopic> topics = feedServiceFacade.list(topicIds);
        Map<Long, FeedTopic> topicsMap = new HashMap<Long, FeedTopic>();
        for (FeedTopic topic : topics) topicsMap.put(topic.getId(), topic);

        PagedList<FeedDto> pagedFeedDtos = new PagedList(totalCount, start, count);
        List<FeedDto> feedDtos = new ArrayList<FeedDto>();
        for (Feed feed : feeds) {
            UserDto user = usersMap.get(feed.getUserId());
            if (user == null) continue;

            feedDtos.add(buildFeedDto(feed, user, topicsMap.get(feed.getTopicId()), staredFeedIds.contains(feed.getId())));
        }
        pagedFeedDtos.setList(feedDtos);

        return pagedFeedDtos;
    }

    private FeedDto buildFeedDto(Feed feed, UserDto user, FeedTopic topic, boolean stared) {
        FeedDto feedDto = new FeedDto();
        feedDto.setId(feed.getId());
        feedDto.setType(feed.getType());
        feedDto.setTopicId(feed.getTopicId());

        if (topic != null && topic.exists()) {
            feedDto.setTopicType(topic.getType());
            feedDto.setTopic(topic.getTitle());
            feedDto.setRefId(topic.getRefId());
        }

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

    private List<String> getChildren(UserDto user) {
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

    @RequestMapping(value = "/topic/{tid}", method = RequestMethod.GET)
    public MomiaHttpResponse topic(@PathVariable(value = "tid") long topicId) {
        if (topicId <= 0) return MomiaHttpResponse.BAD_REQUEST;

        FeedTopic feedTopic = feedServiceFacade.getTopic(topicId);
        FeedTopicDto feedTopicDto = null;
        if (feedTopic.getType() == FeedTopic.Type.PRODUCT) {
            ProductDto product = ProductServiceApi.PRODUCT.get(feedTopic.getRefId(), ProductDto.Type.BASE);
            feedTopicDto = buildFeedTopicDto(feedTopic, product);
        } else if (feedTopic.getType() == FeedTopic.Type.COURSE) {
            // TODO course
        }

        return MomiaHttpResponse.SUCCESS(feedTopicDto);
    }

    @RequestMapping(value = "/topic", method = RequestMethod.GET)
    public MomiaHttpResponse listTopic(@RequestParam int type, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = feedServiceFacade.queryTopicCount(type);
        List<FeedTopic> topics = feedServiceFacade.queryTopic(type, start, count);

        PagedList<FeedTopicDto> pagedFeedTopicDtos = new PagedList(totalCount, start, count);
        List<FeedTopicDto> feedTopicDtos = new ArrayList<FeedTopicDto>();

        Set<Long> refIds = new HashSet<Long>();
        for (FeedTopic feedTopic : topics) refIds.add(feedTopic.getRefId());

        if (type == FeedTopic.Type.PRODUCT) {
            List<ProductDto> products = ProductServiceApi.PRODUCT.list(refIds, ProductDto.Type.BASE);
            Map<Long, ProductDto> productsMap = new HashMap<Long, ProductDto>();
            for (ProductDto product : products) productsMap.put(product.getId(), product);

            for (FeedTopic feedTopic : topics) {
                ProductDto product = productsMap.get(feedTopic.getRefId());
                if (product != null) feedTopicDtos.add(buildFeedTopicDto(feedTopic, product));
            }
        } else if (type == FeedTopic.Type.COURSE) {
            // TODO course
        }

        pagedFeedTopicDtos.setList(feedTopicDtos);

        return MomiaHttpResponse.SUCCESS(pagedFeedTopicDtos);
    }

    private FeedTopicDto buildFeedTopicDto(FeedTopic feedTopic, ProductDto product) {
        FeedTopicDto feedTopicDto = new FeedTopicDto();
        feedTopicDto.setId(feedTopic.getId());
        feedTopicDto.setType(feedTopic.getType());
        feedTopicDto.setRefId(feedTopic.getRefId());
        feedTopicDto.setTitle(StringUtils.isBlank(feedTopic.getTitle()) ? product.getTitle() : feedTopic.getTitle());
        feedTopicDto.setScheduler(product.getScheduler());
        feedTopicDto.setRegion(product.getRegion());

        return feedTopicDto;
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
        if (!feedUser.exists()) return MomiaHttpResponse.FAILED("无效的Feed");

        FeedTopic topic = feed.getTopicId() <= 0 ? FeedTopic.NOT_EXIST_FEED_TOPIC : feedServiceFacade.getTopic(feed.getTopicId());

        boolean stared = userId > 0 && feedServiceFacade.isStared(userId, id);

        return MomiaHttpResponse.SUCCESS(buildFeedDto(feed, feedUser, topic, stared));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public MomiaHttpResponse delete(@RequestParam(value = "uid") long userId, @PathVariable long id) {
        if (userId <= 0 || id <= 0) return MomiaHttpResponse.FAILED("无效的Feed");
        if (!feedServiceFacade.deleteFeed(userId, id)) return MomiaHttpResponse.FAILED("删除Feed失败");
        return MomiaHttpResponse.SUCCESS;
    }
}
