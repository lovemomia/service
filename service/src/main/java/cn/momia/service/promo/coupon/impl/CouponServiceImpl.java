package cn.momia.service.promo.coupon.impl;

import cn.momia.service.common.DbAccessService;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.CouponService;
import cn.momia.service.promo.coupon.UserCoupon;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CouponServiceImpl extends DbAccessService implements CouponService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CouponServiceImpl.class);
    private static final String[] COUPON_FIELDS = { "id", "`type`", "title", "`desc`", "discount", "consumption", "accumulation", "startTime", "endTime" };
    private static final String[] USER_COUPON_FIELDS = { "id", "userId", "couponId", "`type`", "startTime", "orderId", "endTime", "status" };

    @Override
    public Coupon getCoupon(int couponId) {
        String sql = "SELECT " + joinCouponFields() + " FROM t_coupon WHERE id=? AND status=1 AND endTime>NOW()";

        return jdbcTemplate.query(sql, new Object[] { couponId }, new ResultSetExtractor<Coupon>() {
            @Override
            public Coupon extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildCoupon(rs);
                return Coupon.NOT_EXIST_COUPON;
            }
        });
    }

    private String joinCouponFields() {
        return StringUtils.join(COUPON_FIELDS, ",");
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
            coupon.setStartTime(rs.getTimestamp("startTime"));
            coupon.setEndTime(rs.getTimestamp("endTime"));

            return coupon;
        } catch (Exception e) {
            LOGGER.error("fail to build coupon: {}", rs.getInt("id"), e);
            return Coupon.INVALID_COUPON;
        }
    }

    @Override
    public Map<Integer, Coupon> getCoupons(Collection<Integer> couponIds) {
        final Map<Integer, Coupon> coupons = new HashMap<Integer, Coupon>();
        if (couponIds == null || couponIds.isEmpty()) return coupons;

        String sql = "SELECT " + joinCouponFields() + " FROM t_coupon WHERE id IN(" + StringUtils.join(Sets.newHashSet(couponIds), ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Coupon coupon = buildCoupon(rs);
                if (coupon.exists()) coupons.put(coupon.getId(), coupon);
            }
        });

        return coupons;
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
                    String sql = "INSERT INTO t_user_coupon(userId, couponId, `type`, startTime, endTime, addTime) VALUES (?, ?, ?, ?, ?, NOW())";
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, userId);
                    ps.setLong(2, registerCoupon.getId());
                    ps.setInt(3, UserCoupon.Type.REGISTER);
                    ps.setTimestamp(4, new Timestamp(registerCoupon.getStartTime().getTime()));
                    ps.setTimestamp(5, new Timestamp(registerCoupon.getEndTime().getTime()));

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
        String sql = "SELECT " + joinCouponFields() + " FROM t_coupon WHERE status=1 AND `usage`=? AND endTime>NOW() ORDER BY addTime DESC LIMIT 1";

        return jdbcTemplate.query(sql, new Object[] { Coupon.Usage.REGISTER }, new ResultSetExtractor<Coupon>() {
            @Override
            public Coupon extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildCoupon(rs);
                return Coupon.NOT_EXIST_COUPON;
            }
        });
    }

    @Override
    public int queryCountByUser(long userId, long orderId, int status) {
        if (status == UserCoupon.Status.EXPIRED) {
            String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND status<>0 AND endTime<=NOW()";

            return jdbcTemplate.query(sql, new Object[] { userId, UserCoupon.Status.NOT_USED }, new ResultSetExtractor<Integer>() {
                @Override
                public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            });
        } else if (status == UserCoupon.Status.NOT_USED && orderId > 0) {
            String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND (status=? OR (orderId=? AND status=?))";

            return jdbcTemplate.query(sql, new Object[] { userId, status, orderId, UserCoupon.Status.LOCKED }, new ResultSetExtractor<Integer>() {
                @Override
                public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            });
        } else if (status > 0) {
            String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND status=?";

            return jdbcTemplate.query(sql, new Object[] { userId, status }, new ResultSetExtractor<Integer>() {
                @Override
                public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            });
        } else {
            String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND status<>0";

            return jdbcTemplate.query(sql, new Object[] { userId }, new ResultSetExtractor<Integer>() {
                @Override
                public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            });
        }
    }

    @Override
    public List<UserCoupon> queryByUser(long userId, long orderId, int status, int start, int count) {
        final List<UserCoupon> userCoupons = new ArrayList<UserCoupon>();

        if (status == UserCoupon.Status.EXPIRED) {
            String sql = "SELECT " + joinUserCouponFields() + " FROM t_user_coupon WHERE userId=? AND status<>0 AND endTime<=NOW() ORDER BY addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, UserCoupon.Status.NOT_USED, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
                    if (userCoupon.exists()) userCoupons.add(userCoupon);
                }
            });
        } else if (status == UserCoupon.Status.NOT_USED && orderId > 0) {
            String sql = "SELECT " + joinUserCouponFields() + " FROM t_user_coupon WHERE userId=? AND (status=? OR (orderId=? AND status=?)) ORDER BY addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, status, orderId, UserCoupon.Status.LOCKED, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
                    userCoupon.setStatus(UserCoupon.Status.NOT_USED);
                    if (userCoupon.exists()) userCoupons.add(userCoupon);
                }
            });
        } else if (status > 0) {
            String sql = "SELECT " + joinUserCouponFields() + " FROM t_user_coupon WHERE userId=? AND status=? ORDER BY addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, status, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
                    if (userCoupon.exists()) userCoupons.add(userCoupon);
                }
            });
        } else {
            String sql = "SELECT " + joinUserCouponFields() + " FROM t_user_coupon WHERE userId=? AND status<>0 ORDER BY addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
                    if (userCoupon.exists()) userCoupons.add(userCoupon);
                }
            });
        }

        return userCoupons;
    }

    private String joinUserCouponFields() {
        return StringUtils.join(USER_COUPON_FIELDS, ",");
    }

    private UserCoupon buildUserCoupon(ResultSet rs) throws SQLException {
        try {
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setId(rs.getLong("id"));
            userCoupon.setUserId(rs.getLong("userId"));
            userCoupon.setCouponId(rs.getInt("couponId"));
            userCoupon.setType(rs.getInt("type"));
            userCoupon.setStartTime(rs.getTimestamp("startTime"));
            userCoupon.setEndTime(rs.getTimestamp("endTime"));
            userCoupon.setOrderId(rs.getLong("orderId"));
            userCoupon.setStatus(rs.getInt("status"));

            return userCoupon;
        } catch (Exception e) {
            LOGGER.error("fail to build user coupon: {}", rs.getLong("id"), e);
            return UserCoupon.INVALID_USER_COUPON;
        }
    }

    @Override
    public boolean lockUserCoupon(long userId, long orderId, long userCouponId) {
        String sql = "UPDATE t_user_coupon SET orderId=?, status=? WHERE id=? AND userId=? AND (orderId=0 OR orderId=?) AND (status=? OR status=?) AND endTime>NOW()";

        return jdbcTemplate.update(sql, new Object[] { orderId, UserCoupon.Status.LOCKED, userCouponId, userId, orderId, UserCoupon.Status.NOT_USED, UserCoupon.Status.LOCKED }) == 1;
    }

    @Override
    public boolean useUserCoupon(long userId, long orderId, long userCouponId) {
        String sql = "UPDATE t_user_coupon SET status=? WHERE id=? AND userId=? AND orderId=? AND (status=? OR status=?)";

        return jdbcTemplate.update(sql, new Object[] { UserCoupon.Status.USED, userCouponId, userId, orderId, UserCoupon.Status.LOCKED, UserCoupon.Status.USED }) == 1;
    }

    @Override
    public boolean releaseUserCoupon(long userId, long orderId) {
        String sql = "UPDATE t_user_coupon SET orderId=0, status=? WHERE userId=? AND orderId=? AND (status=? OR status=?)";

        return jdbcTemplate.update(sql, new Object[] { UserCoupon.Status.NOT_USED, userId, orderId, UserCoupon.Status.NOT_USED, UserCoupon.Status.LOCKED }) == 1;
    }

    @Override
    public UserCoupon getUserCoupon(long userId, long orderId, long userCouponId) {
        String sql = "SELECT " + joinUserCouponFields() + " FROM t_user_coupon WHERE id=? AND userId=? AND (orderId=0 OR orderId=?) AND (status=? OR status=?) AND startTime<=NOW() AND endTime>NOW()";

        return jdbcTemplate.query(sql, new Object[] { userCouponId, userId, orderId, UserCoupon.Status.NOT_USED, UserCoupon.Status.LOCKED }, new ResultSetExtractor<UserCoupon>() {
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
        if (coupon.exists() && coupon.getConsumption().compareTo(totalFee) <= 0) {
            totalFee = totalFee.subtract(coupon.getDiscount());
        }

        totalFee = totalFee.compareTo(new BigDecimal(0)) < 0 ? new BigDecimal(0) : totalFee;

        return totalFee;
    }

    @Override
    public UserCoupon getNotUsedUserCouponByOrder(long orderId) {
        String sql = "SELECT " + joinUserCouponFields() + " FROM t_user_coupon WHERE orderId=? AND (status=? OR status=?) LIMIT 1";

        return jdbcTemplate.query(sql, new Object[] { orderId, UserCoupon.Status.NOT_USED, UserCoupon.Status.LOCKED }, new ResultSetExtractor<UserCoupon>() {
            @Override
            public UserCoupon extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUserCoupon(rs);
                return UserCoupon.NOT_EXIST_USER_COUPON;
            }
        });
    }
}
