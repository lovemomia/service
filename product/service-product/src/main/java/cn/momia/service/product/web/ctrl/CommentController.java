package cn.momia.service.product.web.ctrl;

import cn.momia.api.product.dto.CommentDto;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.api.dto.PagedList;
import cn.momia.service.comment.Comment;
import cn.momia.service.comment.CommentImage;
import cn.momia.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (id <= 0 || isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = commentService.queryCountByProduct(id);
        List<Comment> comments = commentService.queryByProduct(id, start, count);

        List<Long> userIds = new ArrayList<Long>();
        for (Comment comment : comments) userIds.add(comment.getUserId());
        List<UserDto> users = UserServiceApi.USER.list(userIds, UserDto.Type.MINI);
        Map<Long, UserDto> usersMap = new HashMap<Long, UserDto>();
        for (UserDto user : users) usersMap.put(user.getId(), user);

        PagedList<CommentDto> pagedCommentDtos = new PagedList(totalCount, start, count);
        List<CommentDto> commentDtos = new ArrayList<CommentDto>();
        for (Comment comment : comments) {
            commentDtos.add(buildCommentDto(comment, usersMap.get(comment.getUserId())));
        }
        pagedCommentDtos.setList(commentDtos);

        return MomiaHttpResponse.SUCCESS(pagedCommentDtos);
    }

    private CommentDto buildCommentDto(Comment comment, UserDto user) {
        CommentDto commentDto = new CommentDto();
        commentDto.setUserId(comment.getUserId());
        commentDto.setStar(comment.getStar());
        commentDto.setContent(comment.getContent());
        commentDto.setAddTime(comment.getAddTime());
        commentDto.setImgs(getImgs(comment));

        if (user == null || !user.exists()) {
            commentDto.setNickName("");
            commentDto.setAvatar("");
        } else {
            commentDto.setNickName(user.getNickName());
            commentDto.setAvatar(user.getAvatar());
        }

        return commentDto;
    }

    private List<String> getImgs(Comment comment) {
        List<String> imgs = new ArrayList<String>();
        for (CommentImage commentImage : comment.getImgs()) {
            imgs.add(commentImage.getUrl());
        }

        return imgs;
    }
}
