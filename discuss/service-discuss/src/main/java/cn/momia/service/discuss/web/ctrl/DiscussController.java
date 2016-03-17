package cn.momia.service.discuss.web.ctrl;

import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.core.util.MomiaUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.discuss.DiscussReply;
import cn.momia.service.discuss.DiscussService;
import cn.momia.service.discuss.DiscussTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/discuss")
public class DiscussController extends BaseController {
    @Autowired private DiscussService discussService;

    @RequestMapping(value = "/topic/list", method = RequestMethod.GET)
    public MomiaHttpResponse listTopics(@RequestParam(value = "city") int cityId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        int totalCount = discussService.queryTopicCount(cityId);
        List<DiscussTopic> topics = discussService.queryTopics(cityId, start, count);

        PagedList<DiscussTopic> pagedTopics = new PagedList<DiscussTopic>(totalCount, start, count);
        pagedTopics.setList(topics);

        return MomiaHttpResponse.SUCCESS(pagedTopics);
    }

    @RequestMapping(value = "/topic/{topicid}", method = RequestMethod.GET)
    public MomiaHttpResponse getTopic(@PathVariable(value = "topicid") int topicId) {
        DiscussTopic topic = discussService.getTopic(topicId);
        return topic.exists() ? MomiaHttpResponse.SUCCESS(topic) : MomiaHttpResponse.FAILED("话题不存在");
    }

    @RequestMapping(value = "/topic/{topicid}/reply", method = RequestMethod.GET)
    public MomiaHttpResponse listReplies(@PathVariable(value = "topicid") int topicId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = discussService.queryRepliesCount(topicId);
        List<DiscussReply> replies = discussService.queryReplies(topicId, start, count);

        PagedList<DiscussReply> pagedReplies = new PagedList<DiscussReply>(totalCount, start, count);
        pagedReplies.setList(replies);

        return MomiaHttpResponse.SUCCESS(pagedReplies);
    }

    @RequestMapping(value = "/topic/{topicid}/reply", method = RequestMethod.POST)
    public MomiaHttpResponse reply(@RequestParam(value = "uid") long userId,
                                   @PathVariable(value = "topicid") int topicId,
                                   @RequestParam String content) {
        return MomiaHttpResponse.SUCCESS(discussService.reply(userId, topicId, content));
    }

    @RequestMapping(value = "/reply/filter/notstared", method = RequestMethod.GET)
    public MomiaHttpResponse filterNotStared(@RequestParam(value = "uid") long userId, @RequestParam(value = "replyids") String replyIds) {
        return MomiaHttpResponse.SUCCESS(discussService.filterNotStaredReplyIds(userId, MomiaUtil.splitDistinctLongs(replyIds)));
    }

    @RequestMapping(value = "/reply/{replyid}/star", method = RequestMethod.POST)
    public MomiaHttpResponse star(@RequestParam(value = "uid") long userId, @PathVariable(value = "replyid") long replyId) {
        if (!discussService.exists(replyId)) return MomiaHttpResponse.FAILED("无效的回复，不能点赞");
        return MomiaHttpResponse.SUCCESS(discussService.star(userId, replyId));
    }

    @RequestMapping(value = "/reply/{replyid}/unstar", method = RequestMethod.POST)
    public MomiaHttpResponse unstar(@RequestParam(value = "uid") long userId, @PathVariable(value = "replyid") long replyId) {
        if (!discussService.exists(replyId)) return MomiaHttpResponse.FAILED("无效的回复，不能取消赞");
        return MomiaHttpResponse.SUCCESS(discussService.unstar(userId, replyId));
    }
}
