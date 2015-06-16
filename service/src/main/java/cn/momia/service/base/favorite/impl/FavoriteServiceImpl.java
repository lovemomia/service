package cn.momia.service.base.favorite.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.favorite.FavoriteService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FavoriteServiceImpl extends DbAccessService implements FavoriteService {
    @Override
    public long add(final long userId, final long productId) {
        long favoriteId = getFavored(userId, productId);
        if (favoriteId > 0) return favoriteId;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_favorite(userId, productId, addTime) VALUES(?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, userId);
                ps.setLong(2, productId);

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    private long getFavored(long userId, long productId) {
        String sql = "SELECT id FROM t_favorite WHERE userId=? AND productId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { userId, productId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getLong("id");
                return 0L;
            }
        });
    }

    @Override
    public boolean delete(long userId, long id) {
        String sql = "UPDATE t_favorite SET status=0 WHERE userId=? AND id=?";
        int count = jdbcTemplate.update(sql, new Object[] { userId, id });

        return count > 0;
    }

    @Override
    public List<Long> getFavoritesOfUser(long userId) {
        final List<Long> productIds = new ArrayList<Long>();

        String sql = "SELECT productId FROM t_favorite WHERE userId=? AND status=1";
        jdbcTemplate.query(sql, new Object[] { userId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                productIds.add(rs.getLong("productId"));
            }
        });

        return productIds;
    }
}
