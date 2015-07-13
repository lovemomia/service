package cn.momia.service.promo.coupon.impl;

import cn.momia.service.common.DbAccessService;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.CouponService;
import cn.momia.service.promo.coupon.UserCoupon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CouponServiceImpl extends DbAccessService implements CouponService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Override
    public Coupon getCoupon(int couponId) {
        String sql = "SELECT id, `type`, title, `desc`, discount, consumption, accumulation, duration FROM t_coupon WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { couponId }, new ResultSetExtractor<Coupon>() {
            @Override
            public Coupon extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildCoupon(rs);
                return Coupon.NOT_EXIST_COUPON;
            }
        });
    }

    private Coupon buildCoupon(ResultSet rs) throws SQLException {
        try {
            Coupon coupon = new Coupon();
            coupon.setId(rs.getInt("id"));
            coupon.setType(rs.getInt("type"));
            coupon.setTitle(rs.getString("title"));
            coupon.setDesc(rs.getString("desc"));
            coupon.setDiscount(rs.getBigDecimal("discount"));
            coupon.setConsumption(rs.getBigDecimal("consumption"));
            coupon.setAccumulation(rs.getInt("accumulation"));
            coupon.setDuration(rs.getLong("duration"));

            return coupon;
        } catch (Exception e) {
            LOGGER.error("fail to build coupon: {}", rs.getInt("id"), e);
            return Coupon.INVALID_COUPON;
        }
    }

    @Override
    public long getUserRegisterCoupon(final long userId) {
        try {
            long userCouponId = getUserCoupon(userId, UserCoupon.Type.REGISTER);
            if (userCouponId > 0) return userCouponId;

            final Coupon registerCoupon = getRegisterCoupon();
            if (!registerCoupon.exists()) return 0;

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "INSERT INTO t_user_coupon(userId, couponId, `type`, expiredTime, addTime) VALUES (?, ?, ?, ?, NOW())";
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, userId);
                    ps.setLong(2, registerCoupon.getId());
                    ps.setInt(3, UserCoupon.Type.REGISTER);
                    ps.setTimestamp(4, new Timestamp(new Date().getTime() + registerCoupon.getDuration()));

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

    private Coupon getRegisterCoupon() {
        String sql = "SELECT id, `type`, title, `desc`, discount, consumption, accumulation, duration FROM t_coupon WHERE status=1 AND usage=? ORDER BY addTime DESC LIMIT 1";

        return jdbcTemplate.query(sql, new Object[] { Coupon.Usage.REGISTER }, new ResultSetExtractor<Coupon>() {
            @Override
            public Coupon extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildCoupon(rs);
                return Coupon.NOT_EXIST_COUPON;
            }
        });
    }

    @Override
    public int queryCountByUser(long userId, int status) {
        String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND status=?";

        return jdbcTemplate.query(sql, new Object[] { userId, status }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        });
    }

    @Override
    public List<UserCoupon> queryByUser(long userId, int status, int start, int count) {
        final List<UserCoupon> userCoupons = new ArrayList<UserCoupon>();

        String sql = "SELECT id, userId, couponId, `type`, expiredTime, status FROM t_user_coupon WHERE userId=? AND status=? ORDER BY addTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { userId, status, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                UserCoupon userCoupon = buildUserCoupon(rs);
                if (userCoupon.exists()) userCoupons.add(userCoupon);
            }
        });

        return userCoupons;
    }

    private UserCoupon buildUserCoupon(ResultSet rs) throws SQLException {
        try {
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setId(rs.getLong("id"));
            userCoupon.setUserId(rs.getLong("userId"));
            userCoupon.setCouponId(rs.getInt("couponId"));
            userCoupon.setType(rs.getInt("type"));
            userCoupon.setExpiredTime(rs.getTimestamp("expiredTime"));
            userCoupon.setStatus(rs.getInt("status"));

            return userCoupon;
        } catch (Exception e) {
            LOGGER.error("fail to build user coupon: {}", rs.getLong("id"), e);
            return UserCoupon.INVALID_USER_COUPON;
        }
    }

    @Override
    public UserCoupon lockUserCoupon(long userId, long userCouponId) {
        String sql = "UPDATE t_user_coupon SET status=? WHERE id=? AND userId=? AND status=? AND expiredTime>NOW()";
        boolean successful = jdbcTemplate.update(sql, new Object[] { Coupon.Status.USED, userCouponId, userId, Coupon.Status.NOT_USED }) == 1;
        if (!successful) return UserCoupon.NOT_EXIST_USER_COUPON;

        return getUserCoupon(userId, userCouponId);
    }

    private UserCoupon getUserCoupon(long userId, long userCouponId) {
        String sql = "SELECT id, userId, couponId, `type`, expiredTime, status FROM t_user_coupon WHERE id=? AND userId=?";

        return jdbcTemplate.query(sql, new Object[] { userCouponId, userId }, new ResultSetExtractor<UserCoupon>() {
            @Override
            public UserCoupon extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUserCoupon(rs);
                return UserCoupon.NOT_EXIST_USER_COUPON;
            }
        });
    }

    @Override
    public BigDecimal calcTotalFee(BigDecimal totalFee, Coupon coupon) {
        // TODO 更丰富的优惠方式
        if (coupon.exists() && coupon.getConsumption().compareTo(totalFee) > 0) {
            totalFee = totalFee.subtract(coupon.getDiscount());
            totalFee = totalFee.compareTo(new BigDecimal(0)) < 0 ? new BigDecimal(0) : totalFee;
        }

        return totalFee;
    }
}
