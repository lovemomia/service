package cn.momia.service.feed.comment.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.feed.comment.FeedCommentService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedCommentServiceImpl extends DbAccessService implements FeedCommentService {
    @Override
    public int getCount(long feedId) {
        String sql = "SELECT COUNT(1) FROM t_feed_comment WHERE feedId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { feedId }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getInt(1) : 0;
            }
        });
    }
}
