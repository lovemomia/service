package cn.momia.service.course.subject.coupon.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.subject.coupon.CouponService;
import cn.momia.service.course.subject.coupon.UserCoupon;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CouponServiceImpl extends DbAccessService implements CouponService {
    private static final int NOT_USED_STATUS = 1;
    private static final int USED_STATUS = 2;
    private static final int EXPIRED_STATUS = 3;

    @Override
    public UserCoupon get(long userCouponId) {
        Set<Long> userCouponIds = Sets.newHashSet(userCouponId);
        List<UserCoupon> userCoupons = list(userCouponIds);

        return userCoupons.isEmpty() ? UserCoupon.NOT_EXIST_USER_COUPON : userCoupons.get(0);
    }

    private List<UserCoupon> list(Collection<Long> userCouponIds) {
        if (userCouponIds.isEmpty()) return new ArrayList<UserCoupon>();

        String sql = "SELECT A.Id, B.Type, A.UserId, A.CouponId, B.Title, B.Desc, B.Discount, B.Consumption, B.StartTime, B.EndTime, A.Status FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.Id IN (" + StringUtils.join(userCouponIds, ",") + ") AND A.Status<>0 AND B.Status=1";
        List<UserCoupon> userCoupons = queryList(sql, UserCoupon.class);

        Map<Long, UserCoupon> userCouponsMap = new HashMap<Long, UserCoupon>();
        for (UserCoupon userCoupon : userCoupons) {
            userCouponsMap.put(userCoupon.getId(), userCoupon);
        }

        List<UserCoupon> result = new ArrayList<UserCoupon>();
        for (long userCouponId : userCouponIds) {
            UserCoupon userCoupon = userCouponsMap.get(userCouponId);
            if (userCoupon != null) result.add(userCoupon);
        }

        return result;
    }

    @Override
    public long queryCount(long userId, int status) {
        String sql;
        if (status == NOT_USED_STATUS) {
            sql = "SELECT COUNT(1) FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=1 AND B.EndTime>NOW() AND B.Status=1";
        } else if (status == USED_STATUS) {
            sql = "SELECT COUNT(1) FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=2 AND B.Status=1";
        } else if (status == EXPIRED_STATUS) {
            sql = "SELECT COUNT(1) FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=1 AND B.EndTime<=NOW() AND B.Status=1";
        } else {
            sql = "SELECT COUNT(1) FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND (A.Status=2 OR (A.Status=1 AND B.EndTime>NOW())) AND B.Status=1";
        }

        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<UserCoupon> query(long userId, int status, int start, int count) {
        String sql;
        if (status == NOT_USED_STATUS) {
            sql = "SELECT A.Id FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=1 AND B.EndTime>NOW() AND B.Status=1 ORDER BY B.EndTime ASC, B.StartTime ASC LIMIT ?,?";
        } else if (status == USED_STATUS) {
            sql = "SELECT A.Id FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=2 AND B.Status=1 ORDER BY B.EndTime ASC, B.StartTime ASC LIMIT ?,?";
        } else if (status == EXPIRED_STATUS) {
            sql = "SELECT A.Id FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=1 AND B.EndTime<=NOW() AND B.Status=1 ORDER BY B.EndTime ASC, B.StartTime ASC LIMIT ?,?";
        } else {
            sql = "SELECT A.Id FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND (A.Status=2 OR (A.Status=1 AND B.EndTime>NOW())) AND B.Status=1 ORDER BY B.EndTime ASC, B.StartTime ASC LIMIT ?,?";
        }
        List<Long> userCouponIds = queryLongList(sql, new Object[] { userId, start, count });

        return list(userCouponIds);
    }

    @Override
    public UserCoupon queryByOrder(long orderId) {
        String sql = "SELECT UserCouponId FROM SG_SubjectOrderCoupon WHERE OrderId=? AND Status=1";
        List<Long> userCouponIds = queryLongList(sql, new Object[] { orderId });
        List<UserCoupon> userCoupons = list(userCouponIds);

        return userCoupons.isEmpty() ? UserCoupon.NOT_EXIST_USER_COUPON : userCoupons.get(0);
    }

    @Override
    public BigDecimal calcTotalFee(BigDecimal totalFee, UserCoupon userCoupon) {
        return totalFee.compareTo(userCoupon.getDiscount()) > 0 ? totalFee.subtract(userCoupon.getDiscount()) : new BigDecimal(0);
    }

    @Override
    public boolean preUseCoupon(long orderId, long userCouponId) {
        long orderCouponId = getOrderCouponId(orderId);
        if (orderCouponId <= 0) {
            String sql = "INSERT INTO SG_SubjectOrderCoupon (OrderId, UserCouponId, AddTime) VALUES (?, ?, NOW())";
            return update(sql, new Object[] { orderId, userCouponId });
        } else {
            String sql = "UPDATE SG_SubjectOrderCoupon SET UserCouponId=?, Status=1 WHERE OrderId=?";
            return update(sql, new Object[] { userCouponId, orderId });
        }
    }

    private long getOrderCouponId(long orderId) {
        String sql = "SELECT Id FROM SG_SubjectOrderCoupon WHERE OrderId=?";
        return queryLong(sql, new Object[] { orderId });
    }

    @Override
    public boolean useCoupon(long orderId, long userCouponId) {
        String sql = "UPDATE SG_UserCoupon SET OrderId=? WHERE Id=? AND (OrderId=0 OR OrderId=?)";
        return update(sql, new Object[] { orderId, userCouponId, orderId });
    }
}
