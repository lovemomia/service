package cn.momia.service.feed.comment;

import cn.momia.api.feed.dto.FeedComment;

import java.util.List;

public interface FeedCommentService {
    boolean add(long userId, long feedId, String content);
    boolean delete(long userId, long feedId, long commentId);
    long queryCount(long feedId);
    List<FeedComment> query(long feedId, int start, int count);
}
