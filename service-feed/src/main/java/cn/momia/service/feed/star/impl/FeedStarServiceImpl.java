package cn.momia.service.feed.star.impl;

import cn.momia.common.service.impl.DbAccessService;
import cn.momia.service.feed.star.FeedStarService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeedStarServiceImpl extends DbAccessService implements FeedStarService {
    @Override
    public boolean add(long userId, long feedId) {
        long id = getId(userId, feedId);
        if (id > 0) {
            String sql = "UPDATE t_feed_star SET status=1 WHERE id=? AND userId=? AND feedId=? AND status=0";

            return jdbcTemplate.update(sql, new Object[] { id, userId, feedId }) == 1;
        } else {
            String sql = "INSERT INTO t_feed_star(userId, feedId, addTime) VALUES (?, ?, NOW())";

            return jdbcTemplate.update(sql, new Object[] { userId, feedId }) == 1;
        }
    }

    private long getId(long userId, long feedId) {
        String sql = "SELECT id FROM t_feed_star WHERE userId=? AND feedId=?";

        return jdbcTemplate.query(sql, new Object[] { userId, feedId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong("id") : 0;
            }
        });
    }

    @Override
    public boolean delete(long userId, long feedId) {
        String sql = "UPDATE t_feed_star SET status=0 WHERE userId=? AND feedId=? AND status=1";

        return jdbcTemplate.update(sql, new Object[] { userId, feedId }) == 1;
    }

    @Override
    public int queryUserCount(long feedId) {
        String sql = "SELECT COUNT(DISTINCT userId) FROM t_feed_star WHERE feedId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { feedId }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getInt(1) : 0;
            }
        });
    }

    @Override
    public List<Long> queryUserIds(long feedId, int start, int count) {
        final List<Long> userIds = new ArrayList<Long>();
        String sql = "SELECT userId FROM t_feed_star WHERE feedId=? AND status=1 LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { feedId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long userId = rs.getLong("userId");
                if (userId > 0) userIds.add(userId);
            }
        });

        return userIds;
    }
}
