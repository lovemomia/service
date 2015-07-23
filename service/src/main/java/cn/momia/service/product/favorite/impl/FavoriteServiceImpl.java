package cn.momia.service.product.favorite.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.product.favorite.FavoriteService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FavoriteServiceImpl extends DbAccessService implements FavoriteService {
    @Override
    public boolean isFavoried(long userId, long productId) {
        String sql = "SELECT COUNT(1) FROM t_favorite WHERE userId=? AND productId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { userId, productId }, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getInt(1) > 0 : false;
            }
        });
    }

    @Override
    public boolean favor(long userId, long productId) {
        long id = getId(userId, productId);
        if (id > 0) {
            String sql = "UPDATE t_favorite SET status=1 WHERE id=? AND userId=? AND productId=?";

            return jdbcTemplate.update(sql, new Object[] { id, userId, productId }) > 0;
        } else {
            String sql = "INSERT INTO t_favorite(userId, productId, addTime) VALUES (?, ?, NOW())";

            return jdbcTemplate.update(sql, new Object[] { userId, productId }) > 0;
        }
    }

    private long getId(long userId, long productId) {
        String sql = "SELECT id FROM t_favorite WHERE userId=? AND productId=?";

        return jdbcTemplate.query(sql, new Object[] { userId, productId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong("id") : 0;
            }
        });
    }

    @Override
    public boolean unFavor(long userId, long productId) {
        String sql = "UPDATE t_favorite SET status=0 WHERE userId=? AND productId=?";

        return jdbcTemplate.update(sql, new Object[] { userId, productId }) > 0;
    }
}
