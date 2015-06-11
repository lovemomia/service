package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.comment.Comment;
import cn.momia.service.base.comment.CommentService;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController extends AbstractController {
    @Autowired
    private CommentService commentService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage postComment(@RequestParam String commentJson) {
        Comment comment = new Comment(JSON.parseObject(commentJson));
        long commentId = commentService.add(comment);

        if (commentId <= 0) return new ResponseMessage("fail to post comment");

        comment.setId(commentId);
        return new ResponseMessage(comment);
    }
}
