package cn.momia.service.promo.coupon.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.promo.coupon.UserCouponService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserCouponServiceImpl extends DbAccessService implements UserCouponService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCouponServiceImpl.class);

    private static final String[] USER_COUPON_FIELDS = { "id", "userId", "couponId", "startTime", "orderId", "endTime", "status" };

    @Override
    public void add(List<Object[]> params) {
        String sql = "INSERT INTO t_user_coupon(userId, couponId, src, consumption, startTime, endTime, addTime) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.batchUpdate(sql, params);
    }

    @Override
    public UserCoupon query(long userId, long orderId, long id) {
        String sql = "SELECT " + joinFields() + " FROM t_user_coupon WHERE id=? AND userId=? AND (orderId=0 OR orderId=?) AND (status=? OR status=?) AND startTime<=NOW() AND endTime>NOW()";

        return jdbcTemplate.query(sql, new Object[] { id, userId, orderId, UserCoupon.Status.NOT_USED, UserCoupon.Status.LOCKED }, new ResultSetExtractor<UserCoupon>() {
            @Override
            public UserCoupon extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUserCoupon(rs);
                return UserCoupon.NOT_EXIST_USER_COUPON;
            }
        });
    }

    private String joinFields() {
        return StringUtils.join(USER_COUPON_FIELDS, ",");
    }

    private UserCoupon buildUserCoupon(ResultSet rs) throws SQLException {
        try {
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setId(rs.getLong("id"));
            userCoupon.setUserId(rs.getLong("userId"));
            userCoupon.setCouponId(rs.getInt("couponId"));
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
    public int queryCountByUser(long userId, long orderId, BigDecimal totalFee, int status) {
        if (status == UserCoupon.Status.EXPIRED) {
            String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND status=? AND endTime<=NOW()";

            return jdbcTemplate.query(sql, new Object[] { userId, UserCoupon.Status.NOT_USED }, new ResultSetExtractor<Integer>() {
                @Override
                public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            });
        } else if (status == UserCoupon.Status.NOT_USED && orderId > 0) {
            String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND consumption<=? AND ((status=? AND endTime>NOW()) OR (orderId=? AND status=?))";

            return jdbcTemplate.query(sql, new Object[] { userId, totalFee, status, orderId, UserCoupon.Status.LOCKED }, new ResultSetExtractor<Integer>() {
                @Override
                public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            });
        } else if (status == UserCoupon.Status.NOT_USED) {
            String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND status=? AND endTime>NOW()";

            return jdbcTemplate.query(sql, new Object[] { userId, status }, new ResultSetExtractor<Integer>() {
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
    public List<UserCoupon> queryByUser(long userId, long orderId, BigDecimal totalFee, int status, int start, int count) {
        final List<UserCoupon> userCoupons = new ArrayList<UserCoupon>();

        if (status == UserCoupon.Status.EXPIRED) {
            String sql = "SELECT " + joinFields() + " FROM t_user_coupon WHERE userId=? AND status=? AND endTime<=NOW() ORDER BY endTime ASC, startTime ASC, addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, UserCoupon.Status.NOT_USED, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
                    if (userCoupon.exists()) {
                        userCoupon.setStatus(UserCoupon.Status.EXPIRED);
                        userCoupons.add(userCoupon);
                    }
                }
            });
        } else if (status == UserCoupon.Status.NOT_USED && orderId > 0) {
            String sql = "SELECT " + joinFields() + " FROM t_user_coupon WHERE userId=? AND consumption<=? AND ((status=? AND endTime>NOW()) OR (orderId=? AND status=?)) ORDER BY endTime ASC, startTime ASC, addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, totalFee, status, orderId, UserCoupon.Status.LOCKED, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
                    if (userCoupon.exists()) {
                        userCoupon.setStatus(UserCoupon.Status.NOT_USED);
                        userCoupons.add(userCoupon);
                    }
                }
            });
        } else if (status == UserCoupon.Status.NOT_USED) {
            String sql = "SELECT " + joinFields() + " FROM t_user_coupon WHERE userId=? AND status=? AND endTime>NOW() ORDER BY endTime ASC, startTime ASC, addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, status, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
                    if (userCoupon.exists()) userCoupons.add(userCoupon);
                }
            });
        } else if (status > 0) {
            String sql = "SELECT " + joinFields() + " FROM t_user_coupon WHERE userId=? AND status=? ORDER BY endTime ASC, startTime ASC, addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, status, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
                    if (userCoupon.exists()) userCoupons.add(userCoupon);
                }
            });
        } else {
            String sql = "SELECT " + joinFields() + " FROM t_user_coupon WHERE userId=? AND status<>0 ORDER BY endTime ASC, startTime ASC, addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
                    if (userCoupon.exists()) {
                        if (userCoupon.getStatus() != UserCoupon.Status.USED && userCoupon.isExpired()) userCoupon.setStatus(UserCoupon.Status.EXPIRED);
                        if (userCoupon.getStatus() == UserCoupon.Status.LOCKED) userCoupon.setStatus(UserCoupon.Status.USED);
                        userCoupons.add(userCoupon);
                    }
                }
            });
        }

        return userCoupons;
    }

    @Override
    public int queryCountByUserAndSrc(long userId, int src) {
        String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND src=?";

        return jdbcTemplate.query(sql, new Object[] { userId, src }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getInt(1) : 0;
            }
        });
    }

    @Override
    public UserCoupon queryNotUsedByOrder(long orderId) {
        String sql = "SELECT " + joinFields() + " FROM t_user_coupon WHERE orderId=? AND (status=? OR status=?) LIMIT 1";

        return jdbcTemplate.query(sql, new Object[] { orderId, UserCoupon.Status.NOT_USED, UserCoupon.Status.LOCKED }, new ResultSetExtractor<UserCoupon>() {
            @Override
            public UserCoupon extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildUserCoupon(rs);
                return UserCoupon.NOT_EXIST_USER_COUPON;
            }
        });
    }

    @Override
    public boolean lock(long userId, long orderId, long id) {
        String sql = "UPDATE t_user_coupon SET orderId=?, status=? WHERE id=? AND userId=? AND (orderId=0 OR orderId=?) AND (status=? OR status=?) AND endTime>NOW()";

        return jdbcTemplate.update(sql, new Object[] { orderId, UserCoupon.Status.LOCKED, id, userId, orderId, UserCoupon.Status.NOT_USED, UserCoupon.Status.LOCKED }) == 1;
    }

    @Override
    public boolean use(long userId, long orderId, long id) {
        String sql = "UPDATE t_user_coupon SET status=? WHERE id=? AND userId=? AND orderId=? AND (status=? OR status=?)";

        return jdbcTemplate.update(sql, new Object[] { UserCoupon.Status.USED, id, userId, orderId, UserCoupon.Status.LOCKED, UserCoupon.Status.USED }) == 1;
    }

    @Override
    public boolean release(long userId, long orderId) {
        String sql = "UPDATE t_user_coupon SET orderId=0, status=? WHERE userId=? AND orderId=? AND (status=? OR status=?)";

        return jdbcTemplate.update(sql, new Object[] { UserCoupon.Status.NOT_USED, userId, orderId, UserCoupon.Status.NOT_USED, UserCoupon.Status.LOCKED }) == 1;
    }
}
