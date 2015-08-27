package cn.momia.service.product.comment;

import cn.momia.service.base.Service;

import java.util.List;

public interface CommentService extends Service {
    long add(Comment comment);
    long queryCountByProduct(long productId);
    List<Comment> queryByProduct(long productId, int start, int count);
}
