package cn.momia.service.promo.coupon.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.CouponService;
import cn.momia.service.promo.coupon.UserCoupon;
import com.google.common.collect.Sets;
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
import java.util.Collection;
import java.util.List;

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
    public List<Coupon> getCoupons(Collection<Integer> couponIds) {
        final List<Coupon> coupons = new ArrayList<Coupon>();
        if (couponIds == null || couponIds.isEmpty()) return coupons;

        String sql = "SELECT " + joinCouponFields() + " FROM t_coupon WHERE id IN(" + StringUtils.join(Sets.newHashSet(couponIds), ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Coupon coupon = buildCoupon(rs);
                if (coupon.exists()) coupons.add(coupon);
            }
        });

        return coupons;
    }

    @Override
    public void getUserRegisterCoupons(final long userId) {
        try {
            if (getUserCouponCount(userId, UserCoupon.Type.REGISTER) > 0) return;

            List<Coupon> registerCoupons = getRegisterCoupons();
            if (registerCoupons.isEmpty()) return;

            List<Object[]> params = new ArrayList<Object[]>();
            for (Coupon coupon : registerCoupons) {
                params.add(new Object[] { userId, coupon.getId(), UserCoupon.Type.REGISTER, coupon.getStartTime(), coupon.getEndTime() });
            }

            String sql = "INSERT INTO t_user_coupon(userId, couponId, `type`, startTime, endTime, addTime) VALUES (?, ?, ?, ?, ?, NOW())";
            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            LOGGER.error("fail to get user register coupon for user: {}", userId, e);
        }
    }

    private int getUserCouponCount(long userId, int type) {
        String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND type=? AND status>0";

        return jdbcTemplate.query(sql, new Object[] { userId, type }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getInt(1) : 0;
            }
        });
    }

    private List<Coupon> getRegisterCoupons() {
        final List<Coupon> coupons = new ArrayList<Coupon>();
        String sql = "SELECT " + joinCouponFields() + " FROM t_coupon WHERE status=1 AND `usage`=? AND endTime>NOW() ORDER BY addTime DESC";
        jdbcTemplate.query(sql, new Object[] { Coupon.Usage.REGISTER }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Coupon coupon = buildCoupon(rs);
                if (coupon.exists()) coupons.add(coupon);
            }
        });

        return coupons;
    }

    @Override
    public int queryCountByUser(long userId, long orderId, int status) {
        if (status == UserCoupon.Status.EXPIRED) {
            String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND status=? AND endTime<=NOW()";

            return jdbcTemplate.query(sql, new Object[] { userId, UserCoupon.Status.NOT_USED }, new ResultSetExtractor<Integer>() {
                @Override
                public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            });
        } else if (status == UserCoupon.Status.NOT_USED && orderId > 0) {
            String sql = "SELECT COUNT(1) FROM t_user_coupon WHERE userId=? AND ((status=? AND endTime>NOW()) OR (orderId=? AND status=?))";

            return jdbcTemplate.query(sql, new Object[] { userId, status, orderId, UserCoupon.Status.LOCKED }, new ResultSetExtractor<Integer>() {
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
    public List<UserCoupon> queryByUser(long userId, long orderId, int status, int start, int count) {
        final List<UserCoupon> userCoupons = new ArrayList<UserCoupon>();

        if (status == UserCoupon.Status.EXPIRED) {
            String sql = "SELECT " + joinUserCouponFields() + " FROM t_user_coupon WHERE userId=? AND status=? AND endTime<=NOW() ORDER BY addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, UserCoupon.Status.NOT_USED, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
                    userCoupon.setStatus(UserCoupon.Status.EXPIRED);
                    if (userCoupon.exists()) userCoupons.add(userCoupon);
                }
            });
        } else if (status == UserCoupon.Status.NOT_USED && orderId > 0) {
            String sql = "SELECT " + joinUserCouponFields() + " FROM t_user_coupon WHERE userId=? AND ((status=? AND endTime>NOW()) OR (orderId=? AND status=?)) ORDER BY addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, status, orderId, UserCoupon.Status.LOCKED, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
                    userCoupon.setStatus(UserCoupon.Status.NOT_USED);
                    if (userCoupon.exists()) userCoupons.add(userCoupon);
                }
            });
        } else if (status == UserCoupon.Status.NOT_USED) {
            String sql = "SELECT " + joinUserCouponFields() + " FROM t_user_coupon WHERE userId=? AND status=? AND endTime>NOW() ORDER BY addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, status, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    UserCoupon userCoupon = buildUserCoupon(rs);
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
                    if (userCoupon.getStatus() == UserCoupon.Status.LOCKED) userCoupon.setStatus(UserCoupon.Status.USED);
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
