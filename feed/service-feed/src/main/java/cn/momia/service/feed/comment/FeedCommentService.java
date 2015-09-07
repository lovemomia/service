package cn.momia.service.feed.comment;

import java.util.List;

public interface FeedCommentService {
    boolean add(long userId, long feedId, String content);
    boolean delete(long userId, long feedId, long commentId);
    int queryCount(long feedId);
    List<FeedComment> query(long feedId, int start, int count);
}
