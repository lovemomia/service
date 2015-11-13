package cn.momia.service.feed.comment.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.feed.comment.FeedComment;
import cn.momia.service.feed.comment.FeedCommentService;

import java.util.List;

public class FeedCommentServiceImpl extends AbstractService implements FeedCommentService {
    @Override
    public boolean add(long userId, long feedId, String content) {
        String sql = "INSERT INTO SG_FeedComment(UserId, FeedId, Content, AddTime) VALUES (?, ?, ?, NOW())";
        return update(sql, new Object[] { userId, feedId, content });
    }

    @Override
    public boolean delete(long userId, long feedId, long commentId) {
        String sql = "UPDATE SG_FeedComment SET Status=0 WHERE Id=? AND UserId=? AND FeedId=? AND Status=1";
        return update(sql, new Object[] { commentId, userId, feedId });
    }

    @Override
    public int queryCount(long feedId) {
        String sql = "SELECT COUNT(1) FROM SG_FeedComment WHERE FeedId=? AND Status=1";
        return queryInt(sql, new Object[] { feedId });
    }

    @Override
    public List<FeedComment> query(long feedId, int start, int count) {
        String sql = "SELECT Id, FeedId, UserId, Content, AddTime FROM SG_FeedComment WHERE FeedId=? AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        return queryList(sql, new Object[] { feedId, start, count }, FeedComment.class);
    }
}
