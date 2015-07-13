package cn.momia.service.promo.coupon.impl;

import cn.momia.service.common.DbAccessService;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CouponServiceImpl extends DbAccessService implements CouponService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Override
    public long getUserRegisterCoupon(final long userId) {
        try {
            long userCouponId = getUserCoupon(userId, Coupon.Type.REGISTER);
            if (userCouponId > 0) return userCouponId;

            final long id = getRegisterCoupon();
            if (id <= 0) return 0;

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "INSERT INTO t_user_coupon(userId, couponId, `type`, addTime) VALUES (?, ?, ?, NOW())";
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, userId);
                    ps.setLong(2, id);
                    ps.setInt(3, Coupon.Type.REGISTER);

                    return ps;
                }
            }, keyHolder);

            return keyHolder.getKey().longValue();
        } catch (Exception e) {
            LOGGER.error("fail to get user register coupon for user: {}", userId, e);
            return 0L;
        }
    }

    private long getUserCoupon(long userId, int type) {
        String sql = "SELECT couponId FROM t_user_coupon WHERE userId=? AND type=? AND status>0";

        return jdbcTemplate.query(sql, new Object[] { userId, type }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getLong("couponId");
                return 0L;
            }
        });
    }

    public long getRegisterCoupon() {
        String sql = "SELECT id FROM t_coupon WHERE status=1 AND type=? ORDER BY addTime DESC LIMIT 1";

        return jdbcTemplate.query(sql, new Object[] { Coupon.Type.REGISTER }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getLong("id");
                return 0L;
            }
        });
    }
}
