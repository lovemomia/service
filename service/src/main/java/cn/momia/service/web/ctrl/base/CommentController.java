package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.comment.Comment;
import cn.momia.service.base.comment.CommentService;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentController extends AbstractController {
    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/sku/{skuId}/comment", method = RequestMethod.POST)
    public ResponseMessage postComment(@PathVariable long skuId, @RequestParam int star, @RequestParam String content, @RequestParam long serverId, @RequestParam long customerId) {
        Comment comment = new Comment();
        comment.setStar(star);
        comment.setContent(content);
        comment.setServerId(serverId);
        comment.setCustomerId(customerId);

        long commentId = commentService.add(comment);

        if (commentId <= 0) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to add comment");
        return new ResponseMessage("add comment successfully");
    }

    @RequestMapping(value = "/sku/{skuId}/comment", method = RequestMethod.GET)
    public ResponseMessage getSkuComments(@PathVariable long skuId, @RequestParam int start, @RequestParam int count, @RequestParam(required = false) Integer star) {
        if (star == null) star = Comment.Star.ALL;
        List<Comment> comments = commentService.queryBySku(skuId, star, start, count);

        return new ResponseMessage(comments);
    }

    @RequestMapping(value = "/cn/momia/service/base/user/{userId}/comment", method = RequestMethod.GET)
    public ResponseMessage getUserComments(@PathVariable long userId, @RequestParam int start, @RequestParam int count, @RequestParam(required = false) Integer star) {
        if (star == null) star = Comment.Star.ALL;
        List<Comment> comments = commentService.queryByServer(userId, star, start, count);

        return new ResponseMessage(comments);
    }
}
