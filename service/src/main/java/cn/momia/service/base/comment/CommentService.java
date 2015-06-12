package cn.momia.service.base.comment;

import java.util.List;

public interface CommentService {
    long add(Comment comment,long productId, long skuId);
    Comment get(long id);
    List<Comment> queryBySku(long skuId, int star, int start, int count);
    List<Comment> queryByServer(long serverId, int star, int start, int count);
    List<Comment> queryByProduct(long productId, int star, int start, int count);
}
