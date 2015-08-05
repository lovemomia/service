package cn.momia.service.feed.comment.impl;

import cn.momia.common.service.impl.DbAccessService;
import cn.momia.service.feed.comment.FeedComment;
import cn.momia.service.feed.comment.FeedCommentService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeedCommentServiceImpl extends DbAccessService implements FeedCommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedCommentServiceImpl.class);

    private static final String[] FEED_COMMENT_FIELDS = { "id", "feedId", "userId", "content", "addTime" };

    @Override
    public boolean add(long userId, long feedId, String content) {
        String sql = "INSERT INTO t_feed_comment(userId, feedId, content, addTime) VALUES (?, ?, ?, NOW())";

        return jdbcTemplate.update(sql, new Object[] { userId, feedId, content }) == 1;
    }

    @Override
    public boolean delete(long userId, long feedId, long commentId) {
        String sql = "UPDATE t_feed_comment SET status=0 WHERE id=? AND userId=? AND feedId=?";

        return jdbcTemplate.update(sql, new Object[] { commentId, userId, feedId }) == 1;
    }

    @Override
    public int queryCount(long feedId) {
        String sql = "SELECT COUNT(1) FROM t_feed_comment WHERE feedId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { feedId }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getInt(1) : 0;
            }
        });
    }

    @Override
    public List<FeedComment> query(long feedId, int start, int count) {
        final List<FeedComment> feedComments = new ArrayList<FeedComment>();
        String sql = "SELECT " + joinFields() + " FROM t_feed_comment WHERE feedId=? ORDER BY addTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { feedId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                FeedComment feedComment = buildFeedComment(rs);
                if (feedComment.exists()) feedComments.add(feedComment);
            }
        });

        return feedComments;
    }

    private String joinFields() {
        return StringUtils.join(FEED_COMMENT_FIELDS, ",");
    }

    private FeedComment buildFeedComment(ResultSet rs) throws SQLException {
        try {
            FeedComment feedComment = new FeedComment();
            feedComment.setId(rs.getLong("id"));
            feedComment.setFeedId(rs.getLong("feedId"));
            feedComment.setUserId(rs.getLong("userId"));
            feedComment.setContent(rs.getString("content"));
            feedComment.setAddTime(rs.getTimestamp("addTime"));

            return feedComment;
        } catch (Exception e) {
            LOGGER.error("fail to build feed comment: {}", rs.getLong("id"), e);
            return FeedComment.INVALID_FEED_COMMENT;
        }
    }
}
