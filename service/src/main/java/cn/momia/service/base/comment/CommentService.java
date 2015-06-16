package cn.momia.service.base.comment;

import java.util.List;

public interface CommentService {
    long add(Comment comment);
    Comment get(long id);
    List<Comment> queryByProduct(long productId, int start, int count);
}
