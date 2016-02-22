package cn.momia.service.feed.web.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.core.dto.PagedList;
import cn.momia.service.feed.base.Feed;
import cn.momia.service.feed.base.FeedService;
import cn.momia.service.feed.base.FeedTag;
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

import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedController.class);

    @Autowired private FeedService feedService;

    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    public MomiaHttpResponse follow(@RequestParam(value = "uid") long userId, @RequestParam(value = "fuid") long followedId) {
        return MomiaHttpResponse.SUCCESS(feedService.isFollowed(userId, followedId) || feedService.follow(userId, followedId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam(value = "uid") long userId,
                                  @RequestParam int start,
                                  @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = userId > 0 ? feedService.queryFollowedCountByUser(userId) : feedService.queryOfficialFeedsCount();
        List<Feed> feeds = userId > 0 ? feedService.queryFollowedByUser(userId, start, count) : feedService.queryOfficialFeeds(start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedFeeds(feeds, totalCount, start, count));
    }

    private PagedList<Feed> buildPagedFeeds(List<Feed> feeds, long totalCount, int start, int count) {
        PagedList<Feed> pagedFeeds = new PagedList<Feed>(totalCount, start, count);
        pagedFeeds.setList(feeds);

        return pagedFeeds;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public MomiaHttpResponse listFeedsOfUser(@RequestParam(value = "uid") long userId,
                                             @RequestParam int start,
                                             @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount =feedService.queryCountByUser(userId);
        List<Feed> feeds = feedService.queryByUser(userId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedFeeds(feeds, totalCount, start, count));
    }

    @RequestMapping(value = "/subject", method = RequestMethod.GET)
    public MomiaHttpResponse queryBySubject(@RequestParam(value = "suid") long subjectId,
                                            @RequestParam int start,
                                            @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = feedService.queryCountBySubject(subjectId);
        List<Feed> feeds = feedService.queryBySubject(subjectId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedFeeds(feeds, totalCount, start, count));
    }

    @RequestMapping(value = "/course", method = RequestMethod.GET)
    public MomiaHttpResponse queryByCourse(@RequestParam(value = "coid") long courseId,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = feedService.queryCountByCourse(courseId);
        List<Feed> feeds = feedService.queryByCourse(courseId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedFeeds(feeds, totalCount, start, count));
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
        if (isOfficialUser) feed.setOfficial(true);

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
    public MomiaHttpResponse get(@PathVariable(value = "fid") long feedId) {
        Feed feed = feedService.get(feedId);
        return feed.exists() ? MomiaHttpResponse.SUCCESS(feed) : MomiaHttpResponse.FAILED("无效的Feed");
    }

    @RequestMapping(value = "/{fid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse delete(@RequestParam(value = "uid") long userId, @PathVariable(value = "fid") long feedId) {
        return MomiaHttpResponse.SUCCESS(feedService.delete(userId, feedId));
    }
}
