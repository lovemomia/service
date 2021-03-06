package cn.momia.service.course.coupon.impl;

import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.service.AbstractService;
import cn.momia.common.core.util.TimeUtil;
import cn.momia.api.course.dto.coupon.Coupon;
import cn.momia.api.course.dto.coupon.CouponCode;
import cn.momia.service.course.coupon.CouponService;
import cn.momia.service.course.coupon.InviteCoupon;
import cn.momia.api.course.dto.coupon.UserCoupon;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CouponServiceImpl extends AbstractService implements CouponService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CouponServiceImpl.class);

    private static final int NOT_USED_STATUS = 1;
    private static final int USED_STATUS = 2;
    private static final int EXPIRED_STATUS = 3;

    @Override
    public Coupon getCoupon(int couponId) {
        List<Coupon> coupons = listCoupons(Sets.newHashSet(couponId));
        return coupons.isEmpty() ? Coupon.NOT_EXISTS_COUPON : coupons.get(0);
    }

    private List<Coupon> listCoupons(Collection<Integer> couponIds) {
        if (couponIds.isEmpty()) return new ArrayList<Coupon>();

        String sql = "SELECT Id, Src, Count, Discount, TimeType, Time, TimeUnit, StartTime, EndTime FROM SG_Coupon WHERE Id IN(" + StringUtils.join(couponIds, ",") + ") AND OnlineTime<=NOW() AND OfflineTime>NOW() AND Status<>0";
        List<Coupon> coupons = queryObjectList(sql, Coupon.class);

        Map<Integer, Coupon> couponsMap = new HashMap<Integer, Coupon>();
        for (Coupon coupon : coupons) {
            couponsMap.put(coupon.getId(), coupon);
        }

        List<Coupon> result = new ArrayList<Coupon>();
        for (int couponId : couponIds) {
            Coupon coupon = couponsMap.get(couponId);
            if (coupon != null) result.add(coupon);
        }

        return result;
    }

    @Override
    public UserCoupon get(long userCouponId) {
        Set<Long> userCouponIds = Sets.newHashSet(userCouponId);
        List<UserCoupon> userCoupons = listUserCoupons(userCouponIds);

        return userCoupons.isEmpty() ? UserCoupon.NOT_EXIST_USER_COUPON : userCoupons.get(0);
    }

    private List<UserCoupon> listUserCoupons(Collection<Long> userCouponIds) {
        if (userCouponIds.isEmpty()) return new ArrayList<UserCoupon>();

        String sql = "SELECT A.Id, B.Type, A.UserId, A.CouponId, B.Title, B.Desc, B.Discount, B.Consumption, A.StartTime, A.EndTime, A.InviteCode, A.Status FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.Id IN (" + StringUtils.join(userCouponIds, ",") + ") AND A.Status<>0 AND B.Status<>0";
        List<UserCoupon> userCoupons = queryObjectList(sql, UserCoupon.class);

        Map<Long, UserCoupon> userCouponsMap = new HashMap<Long, UserCoupon>();
        for (UserCoupon userCoupon : userCoupons) {
            userCouponsMap.put(userCoupon.getId(), userCoupon);
        }

        Date now = new Date();
        List<UserCoupon> result = new ArrayList<UserCoupon>();
        for (long userCouponId : userCouponIds) {
            UserCoupon userCoupon = userCouponsMap.get(userCouponId);
            if (userCoupon != null) {
                int status = userCoupon.getStatus();
                if (status != UserCoupon.Status.USED && userCoupon.getEndTime().before(now)) userCoupon.setStatus(UserCoupon.Status.EXPIRED);
                result.add(userCoupon);
            }
        }

        return result;
    }

    @Override
    public BigDecimal calcTotalFee(BigDecimal totalFee, UserCoupon userCoupon) {
        return totalFee.compareTo(userCoupon.getDiscount()) > 0 ? totalFee.subtract(userCoupon.getDiscount()) : new BigDecimal(0);
    }

    @Override
    public long queryCount(long userId, int status) {
        String sql;
        if (status == NOT_USED_STATUS) {
            sql = "SELECT COUNT(1) FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=1 AND A.EndTime>NOW() AND B.Status<>0";
        } else if (status == USED_STATUS) {
            sql = "SELECT COUNT(1) FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=2 AND B.Status<>0";
        } else if (status == EXPIRED_STATUS) {
            sql = "SELECT COUNT(1) FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=1 AND A.EndTime<=NOW() AND B.Status<>0";
        } else {
            sql = "SELECT COUNT(1) FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND (A.Status=2 OR (A.Status=1 AND A.EndTime>NOW())) AND B.Status<>0";
        }

        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<UserCoupon> query(long userId, int status, int start, int count) {
        String sql;
        if (status == NOT_USED_STATUS) {
            sql = "SELECT A.Id FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=1 AND A.EndTime>NOW() AND B.Status<>0 ORDER BY A.EndTime ASC, A.StartTime ASC LIMIT ?,?";
        } else if (status == USED_STATUS) {
            sql = "SELECT A.Id FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=2 AND B.Status<>0 ORDER BY A.EndTime ASC, A.StartTime ASC LIMIT ?,?";
        } else if (status == EXPIRED_STATUS) {
            sql = "SELECT A.Id FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND A.Status=1 AND A.EndTime<=NOW() AND B.Status<>0 ORDER BY A.EndTime ASC, A.StartTime ASC LIMIT ?,?";
        } else {
            sql = "SELECT A.Id FROM SG_UserCoupon A INNER JOIN SG_Coupon B ON A.CouponId=B.Id WHERE A.UserId=? AND (A.Status=2 OR (A.Status=1 AND A.EndTime>NOW())) AND B.Status<>0 ORDER BY A.Status ASC, A.EndTime ASC, A.StartTime ASC LIMIT ?,?";
        }
        List<Long> userCouponIds = queryLongList(sql, new Object[] { userId, start, count });

        return listUserCoupons(userCouponIds);
    }

    @Override
    public UserCoupon queryByOrder(long orderId) {
        String sql = "SELECT UserCouponId FROM SG_SubjectOrderCoupon WHERE OrderId=? AND Status<>0";
        List<Long> userCouponIds = queryLongList(sql, new Object[] { orderId });
        List<UserCoupon> userCoupons = listUserCoupons(userCouponIds);

        return userCoupons.isEmpty() ? UserCoupon.NOT_EXIST_USER_COUPON : userCoupons.get(0);
    }

    @Override
    public UserCoupon queryUsedByOrder(long orderId) {
        String sql = "SELECT Id FROM SG_UserCoupon WHERE OrderId=? AND Status=2";
        List<Long> userCouponIds = queryLongList(sql, new Object[] { orderId });
        List<UserCoupon> userCoupons = listUserCoupons(userCouponIds);

        return userCoupons.isEmpty() ? UserCoupon.NOT_EXIST_USER_COUPON : userCoupons.get(0);
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
        String sql = "UPDATE SG_UserCoupon SET OrderId=?, Status=2 WHERE Id=? AND (OrderId=0 OR OrderId=?)";
        return update(sql, new Object[] { orderId, userCouponId, orderId });
    }

    @Override
    public boolean hasInviteCoupon(String mobile) {
        String sql = "SELECT COUNT(1) FROM SG_InviteCoupon WHERE Mobile=? AND Status=1";
        return queryInt(sql, new Object[] { mobile }) > 0;
    }

    @Override
    public boolean addInviteCoupon(String mobile, String inviteCode) {
        String sql = "SELECT Id FROM SG_Coupon WHERE Src=? AND OnlineTime<=NOW() AND OfflineTime>NOW() AND Status<>0 ORDER BY AddTime DESC LIMIT 1";
        List<Integer> couponIds = queryIntList(sql, new Object[] { Coupon.Src.INVITE });

        List<Coupon> coupons = listCoupons(couponIds);
        if (!coupons.isEmpty()) return addInviteCoupon(mobile, inviteCode, coupons.get(0));

        throw new MomiaErrorException("不能再领取了，活动已经结束了哦");
    }

    private boolean addInviteCoupon(String mobile, String inviteCode, Coupon coupon) {
        String sql = "INSERT INTO SG_InviteCoupon (Mobile, InviteCode, CouponId, AddTime) VALUES (?, ?, ?, NOW())";
        try {
            return update(sql, new Object[] { mobile, inviteCode, coupon.getId() });
        } catch (Exception e) {
            LOGGER.error("fail to add invite coupon for mobile: {}", mobile, e);
            return false;
        }
    }

    @Override
    public InviteCoupon getInviteCoupon(String mobile) {
        String sql = "SELECT Id, Mobile, InviteCode, CouponId FROM SG_InviteCoupon WHERE Mobile=? AND Status=1";
        List<InviteCoupon> inviteCoupons = queryObjectList(sql, new Object[] { mobile }, InviteCoupon.class);

        return inviteCoupons.isEmpty() ? InviteCoupon.NOT_EXIST_INVITE_COUPON : inviteCoupons.get(0);
    }

    @Override
    public boolean updateInviteCouponStatus(String mobile) {
        String sql = "UPDATE SG_InviteCoupon SET Status=2 WHERE Mobile=? AND Status=1";
        return update(sql, new Object[] { mobile });
    }

    @Override
    public UserCoupon distributeInviteUserCoupon(long userId, int couponId, String inviteCode) {
        List<Coupon> coupons = listCoupons(Sets.newHashSet(couponId));
        if (coupons.isEmpty()) return UserCoupon.NOT_EXIST_USER_COUPON;

        return addUserCoupon(userId, StringUtils.isBlank(inviteCode) ? "" : inviteCode, coupons.get(0));
    }

    private UserCoupon addUserCoupon(final long userId, final String inviteCode, final Coupon coupon) {
        long userCouponId = doAddUserCoupon(userId, inviteCode, coupon);
        return userCouponId > 0 ? getUserCoupon(userCouponId) : UserCoupon.NOT_EXIST_USER_COUPON;
    }

    private long doAddUserCoupon(final long userId, final String inviteCode, final Coupon coupon) {
        KeyHolder keyHolder = insert(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_UserCoupon (UserId, CouponId, StartTime, EndTime, InviteCode, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, userId);
                ps.setInt(2, coupon.getId());

                int timeType = coupon.getTimeType();
                Date startTime;
                Date endTime;
                if (timeType == 1) {
                    startTime = new Date();
                    endTime = TimeUtil.add(startTime, coupon.getTime(), coupon.getTimeUnit());
                } else {
                    startTime = coupon.getStartTime();
                    endTime = coupon.getEndTime();
                }
                ps.setTimestamp(3, new Timestamp(startTime.getTime()));
                ps.setTimestamp(4, new Timestamp(endTime.getTime()));

                ps.setString(5, inviteCode);

                return ps;
            }
        });

        return keyHolder.getKey().longValue();
    }

    private UserCoupon getUserCoupon(long userCouponId) {
        Set<Long> userCouponIds = Sets.newHashSet(userCouponId);
        List<UserCoupon> userCoupons = listUserCoupons(userCouponIds);

        return userCoupons.isEmpty() ? UserCoupon.NOT_EXIST_USER_COUPON : userCoupons.get(0);
    }

    @Override
    public UserCoupon distributeFirstPayUserCoupon(long userId) {
        List<Coupon> coupons = listFirstPayCoupons();
        if (coupons.isEmpty()) return UserCoupon.NOT_EXIST_USER_COUPON;

        return addUserCoupon(userId, "", coupons.get(0));
    }

    private List<Coupon> listFirstPayCoupons() {
        String sql = "SELECT Id FROM SG_Coupon WHERE Src=? AND OnlineTime<=NOW() AND OfflineTime>NOW() AND Status<>0 ORDER BY AddTime DESC LIMIT 1";
        List<Integer> couponIds = queryIntList(sql, new Object[] { Coupon.Src.FIRST_PAY });

        return listCoupons(couponIds);
    }

    @Override
    public List<UserCoupon> queryUserCouponsToExpired(int days) {
        Date now = new Date();
        String lower = TimeUtil.SHORT_DATE_FORMAT.format(new Date(now.getTime() + (days + 1L) * 24 * 60 * 60 * 1000));
        String upper = TimeUtil.SHORT_DATE_FORMAT.format(new Date(now.getTime() + (days + 2L) * 24 * 60 * 60 * 1000));

        String sql = "SELECT Id FROM SG_UserCoupon WHERE Status=? AND EndTime>=? AND EndTime<?";
        List<Long> userCouponIds = queryLongList(sql, new Object[] { UserCoupon.Status.NOT_USED, lower, upper });

        return listUserCoupons(userCouponIds);
    }

    @Override
    public CouponCode getCouponCode(String code) {
        String sql = "SELECT Id, Code, Discount, Consumption FROM SG_CouponCode WHERE Code=? AND OnlineTime<=NOW() AND OfflineTime>NOW() AND Status=1";
        List<CouponCode> couponCodes = queryObjectList(sql, new Object[] { code }, CouponCode.class);
        return couponCodes.isEmpty() ? CouponCode.NOT_EXIST_COUPON_CODE : couponCodes.get(0);
    }

    @Override
    public boolean hasActivityCoupon(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_ActivityCoupon WHERE UserId=? AND Status=1";
        return queryInt(sql, new Object[] { userId }) > 0;
    }

    @Override
    public long distributeActivityCoupon(final long userId, final Coupon coupon) {
        String sql = "INSERT INTO SG_ActivityCoupon (UserId, AddTime) VALUES (?, NOW())";
        if (!update(sql, new Object[] { userId })) {
            LOGGER.error("fail to insert into SG_ActivityCoupon for user: {}", userId);
            return 0;
        }

        return doAddUserCoupon(userId, "", coupon);
    }

    @Override
    public List<Coupon> getCouponsBySrc(int src) {
        String sql = "SELECT Id FROM SG_Coupon WHERE Src=? AND Status=1";
        return listCoupons(queryIntList(sql, new Object[] { src }));
    }

    @Override
    public boolean distributeActivityCoupons(final long userId, final List<Coupon> coupons) {
        try {
            execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    String sql = "INSERT INTO SG_ActivityCoupon (UserId, AddTime) VALUES (?, NOW())";
                    if (!update(sql, new Object[] { userId })) {
                        LOGGER.error("fail to insert into SG_ActivityCoupon for user: {}", userId);
                        throw new MomiaErrorException("分发红包失败");
                    }

                    for (Coupon coupon : coupons) {
                        for (int i = 0; i < coupon.getCount(); i++) {
                            doAddUserCoupon(userId, "", coupon);
                        }
                    }

                    return null;
                }
            });
            return true;
        } catch (Exception e) {
            LOGGER.error("分发红包失败，用户ID: {}", userId, e);
        }

        return false;
    }
}
