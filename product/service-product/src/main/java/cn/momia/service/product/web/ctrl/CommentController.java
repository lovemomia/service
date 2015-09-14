package cn.momia.service.product.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.webapp.ctrl.dto.PagedListDto;
import cn.momia.service.comment.Comment;
import cn.momia.service.comment.CommentService;
import cn.momia.service.product.web.ctrl.dto.CommentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class CommentController extends BaseController {
    @Autowired private CommentService commentService;

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse add(@RequestBody Comment comment) {
        if (comment.isInvalid()) return MomiaHttpResponse.FAILED("无效的评论，参数不完整");

        if (commentService.add(comment) <= 0) return MomiaHttpResponse.FAILED("发表评论失败");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.GET)
    public MomiaHttpResponse listComments(@PathVariable long id, @RequestParam int start, @RequestParam int count){
        if (id <= 0 || isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedListDto.EMPTY);

        long totalCount = commentService.queryCountByProduct(id);
        List<Comment> comments = commentService.queryByProduct(id, start, count);

        PagedListDto commentsDto = new PagedListDto(totalCount, start, count);
        for (Comment comment : comments) commentsDto.add(new CommentDto(comment));

        return MomiaHttpResponse.SUCCESS(commentsDto);
    }
}
