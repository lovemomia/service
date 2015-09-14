package cn.momia.service.promo.coupon.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.CouponService;
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

    private static final String[] COUPON_FIELDS = { "id", "`type`", "`count`", "title", "`desc`", "discount", "consumption", "accumulation", "startTime", "endTime" };

    @Override
    public Coupon get(int couponId) {
        if (couponId <= 0) return Coupon.NOT_EXIST_COUPON;

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
            coupon.setCount(rs.getInt("count"));
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
    public List<Coupon> list(Collection<Integer> couponIds) {
        if (couponIds.isEmpty()) return new ArrayList<Coupon>();

        final List<Coupon> coupons = new ArrayList<Coupon>();
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
    public List<Coupon> queryBySrc(int src) {
        final List<Coupon> coupons = new ArrayList<Coupon>();
        String sql = "SELECT " + joinCouponFields() + " FROM t_coupon WHERE status=1 AND src=? AND onlineTime<=NOW() AND offlineTime>NOW() ORDER BY addTime DESC";
        jdbcTemplate.query(sql, new Object[] { src }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Coupon coupon = buildCoupon(rs);
                if (coupon.exists()) coupons.add(coupon);
            }
        });

        return coupons;
    }

    @Override
    public List<Coupon> queryBySrcAndDiscount(int src, int discount) {
        final List<Coupon> coupons = new ArrayList<Coupon>();
        String sql = "SELECT " + joinCouponFields() + " FROM t_coupon WHERE status=1 AND src=? AND discount=? AND onlineTime<=NOW() AND offlineTime>NOW() ORDER BY addTime DESC";
        jdbcTemplate.query(sql, new Object[] { src, discount }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Coupon coupon = buildCoupon(rs);
                if (coupon.exists()) coupons.add(coupon);
            }
        });

        return coupons;
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
}
