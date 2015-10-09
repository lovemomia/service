package cn.momia.service.favorite.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.favorite.FavoriteService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteServiceImpl extends DbAccessService implements FavoriteService {
    private static final int TYPE_PRODUCT = 1;

    @Override
    public boolean isFavoried(long userId, long productId) {
        String sql = "SELECT COUNT(1) FROM t_favorite WHERE userId=? AND `type`=? AND refId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { userId, TYPE_PRODUCT, productId }, new ResultSetExtractor<Boolean>() {
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
            String sql = "UPDATE t_favorite SET status=1 WHERE id=? AND userId=? AND `type`=? AND refId=?";
            return jdbcTemplate.update(sql, new Object[] { id, userId, TYPE_PRODUCT, productId }) == 1;
        } else {
            String sql = "INSERT INTO t_favorite(userId, `type`, refId, addTime) VALUES (?, ?, ?, NOW())";
            return jdbcTemplate.update(sql, new Object[] { userId, TYPE_PRODUCT, productId }) == 1;
        }
    }

    private long getId(long userId, long productId) {
        String sql = "SELECT id FROM t_favorite WHERE userId=? AND `type`=? AND refId=?";

        return jdbcTemplate.query(sql, new Object[] { userId, TYPE_PRODUCT, productId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong("id") : 0;
            }
        });
    }

    @Override
    public boolean unFavor(long userId, long productId) {
        String sql = "UPDATE t_favorite SET status=0 WHERE userId=? AND `type`=? AND refId=?";
        return jdbcTemplate.update(sql, new Object[] { userId, TYPE_PRODUCT, productId }) > 0;
    }

    @Override
    public long queryCount(long userId) {
        String sql = "SELECT COUNT(1) FROM t_favorite WHERE userId=? AND `type`=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { userId, TYPE_PRODUCT }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<Long> query(long userId, int start, int count) {
        final List<Long> productIds = new ArrayList<Long>();
        String sql = "SELECT refId FROM t_favorite WHERE userId=? AND `type`=? AND status=1 ORDER BY updateTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { userId, TYPE_PRODUCT, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                productIds.add(rs.getLong("refId"));
            }
        });

        return productIds;
    }
}
