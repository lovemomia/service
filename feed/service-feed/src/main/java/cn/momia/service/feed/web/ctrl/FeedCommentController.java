package cn.momia.service.feed.web.ctrl;

import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.feed.base.FeedService;
import cn.momia.service.feed.comment.FeedComment;
import cn.momia.service.feed.comment.FeedCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedCommentController extends BaseController {
    @Autowired private FeedService feedService;
    @Autowired private FeedCommentService feedCommentService;

    @RequestMapping(value = "/{fid}/comment/list", method = RequestMethod.GET)
    public MomiaHttpResponse listComments(@PathVariable(value = "fid") long feedId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = feedCommentService.queryCount(feedId);
        List<FeedComment> comments = feedCommentService.query(feedId, start, count);

        PagedList<FeedComment> pagedFeedComments = new PagedList<FeedComment>(totalCount, start, count);
        pagedFeedComments.setList(comments);

        return MomiaHttpResponse.SUCCESS(pagedFeedComments);
    }

    @RequestMapping(value = "/{fid}/comment", method = RequestMethod.POST)
    public MomiaHttpResponse addComment(@RequestParam(value = "uid") long userId,
                                        @PathVariable(value = "fid") long feedId,
                                        @RequestParam String content) {
        if (!feedCommentService.add(userId, feedId, content)) return MomiaHttpResponse.FAILED("发表评论失败");
        feedService.increaseCommentCount(feedId);
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{fid}/comment/{cmid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse deleteComment(@RequestParam(value = "uid") long userId,
                                           @PathVariable(value = "fid") long feedId,
                                           @PathVariable(value = "cmid") long commentId) {
        if (!feedCommentService.delete(userId, feedId, commentId)) return MomiaHttpResponse.FAILED("删除评论失败");
        feedService.decreaseCommentCount(feedId);
        return MomiaHttpResponse.SUCCESS;
    }
}
