package cn.momia.service.course.order.impl;

import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.core.util.TimeUtil;
import cn.momia.common.service.AbstractService;
import cn.momia.api.course.dto.subject.Subject;
import cn.momia.service.course.order.Order;
import cn.momia.service.course.order.OrderService;
import cn.momia.service.course.order.OrderPackage;
import cn.momia.service.course.order.Payment;
import cn.momia.service.course.order.Refund;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderServiceImpl extends AbstractService implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public long add(final Order order) {
        KeyHolder keyHolder = insert(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_SubjectOrder(UserId, SubjectId, Contact, Mobile, InviteCode, CouponCode, AddTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, order.getUserId());
                ps.setLong(2, order.getSubjectId());
                ps.setString(3, order.getContact());
                ps.setString(4, order.getMobile());
                ps.setString(5, order.getInviteCode());
                ps.setString(6, order.getCouponCode());

                return ps;
            }
        });

        long orderId = keyHolder.getKey().longValue();
        if (orderId < 0) throw new MomiaErrorException("下单失败");

        addOrderPackages(orderId, order);

        return orderId;
    }

    private void addOrderPackages(long orderId, Order order) {
        String sql = "INSERT INTO SG_SubjectOrderPackage (UserId, OrderId, SkuId, Price, CourseCount, BookableCount, Time, TimeUnit, AddTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        List<Object[]> args = new ArrayList<Object[]>();
        for (OrderPackage orderPackage : order.getPackages()) {
            args.add(new Object[] { order.getUserId(), orderId, orderPackage.getSkuId(), orderPackage.getPrice(), orderPackage.getBookableCount(), orderPackage.getBookableCount(), orderPackage.getTime(), orderPackage.getTimeUnit() });
        }
        batchUpdate(sql, args);
    }

    @Override
    public Order get(long orderId) {
        Set<Long> orderIds = Sets.newHashSet(orderId);
        List<Order> orders = list(orderIds);

        return orders.isEmpty() ? Order.NOT_EXIST_ORDER : orders.get(0);
    }

    @Override
    public List<Order> list(Collection<Long> orderIds) {
        if (orderIds.isEmpty()) return new ArrayList<Order>();

        String sql = "SELECT Id, UserId, SubjectId, Contact, Mobile, RefundMessage, Status, AddTime FROM SG_SubjectOrder WHERE Id IN (" + StringUtils.join(orderIds, ",") + ") AND Status<>0";
        List<Order> orders = queryObjectList(sql, Order.class);

        Map<Long, List<OrderPackage>> packagesMap = queryOrderPackages(orderIds);
        Set<Long> skuIds = new HashSet<Long>();
        for (List<OrderPackage> packages : packagesMap.values()) {
            for (OrderPackage orderPackage : packages) {
                skuIds.add(orderPackage.getSkuId());
            }
        }

        for (Order order : orders) {
            List<OrderPackage> packages = packagesMap.get(order.getId());
            order.setPackages(packages);
        }

        Map<Long, Order> ordersMap = new HashMap<Long, Order>();
        for (Order order : orders) {
            ordersMap.put(order.getId(), order);
        }

        List<Order> result = new ArrayList<Order>();
        for (long orderId : orderIds) {
            Order order = ordersMap.get(orderId);
            if (order != null) result.add(order);
        }

        return result;
    }

    private Map<Long, List<OrderPackage>> queryOrderPackages(Collection<Long> orderIds) {
        if (orderIds.isEmpty()) return new HashMap<Long, List<OrderPackage>>();

        String sql = "SELECT Id FROM SG_SubjectOrderPackage WHERE OrderId IN (" + StringUtils.join(orderIds, ",") + ") AND Status<>0";
        List<Long> packageIds = queryLongList(sql);
        List<OrderPackage> packages = listOrderPackages(packageIds);

        Map<Long, List<OrderPackage>> packagesMap = new HashMap<Long, List<OrderPackage>>();
        for (long orderId : orderIds) {
            packagesMap.put(orderId, new ArrayList<OrderPackage>());
        }
        for (OrderPackage orderPackage : packages) {
            packagesMap.get(orderPackage.getOrderId()).add(orderPackage);
        }

        return packagesMap;
    }

    private List<OrderPackage> listOrderPackages(Collection<Long> packageIds) {
        if (packageIds.isEmpty()) return new ArrayList<OrderPackage>();

        String sql = "SELECT A.Id, A.UserId, A.OrderId, A.SkuId, A.Price, A.CourseCount, A.BookableCount, A.Time, A.TimeUnit, B.CourseId FROM SG_SubjectOrderPackage A INNER JOIN SG_SubjectSku B ON A.SkuId=B.Id WHERE A.Id IN (" + StringUtils.join(packageIds, ",") + ") AND A.Status<>0 AND B.Status<>0";
        List<OrderPackage> packages = queryObjectList(sql, OrderPackage.class);

        Map<Long, OrderPackage> packagesMap = new HashMap<Long, OrderPackage>();
        for (OrderPackage orderPackage : packages) {
            packagesMap.put(orderPackage.getId(), orderPackage);
        }

        List<OrderPackage> result = new ArrayList<OrderPackage>();
        for (long packageId : packageIds) {
            OrderPackage orderPackage = packagesMap.get(packageId);
            if (orderPackage != null) result.add(orderPackage);
        }

        return result;
    }

    @Override
    public boolean delete(long userId, long orderId) {
        String sql = "UPDATE SG_SubjectOrder SET Status=0 WHERE UserId=? AND Id=? AND Status<?";
        return update(sql, new Object[] { userId, orderId, Order.Status.PAYED });
    }

    @Override
    public boolean applyRefund(final long userId, final BigDecimal fee, final String message, final Payment payment) {
        try {
            execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    String sql = "UPDATE SG_SubjectOrder SET Status=?, RefundMessage=? WHERE UserId=? AND Id=? AND Status=?";
                    if (update(sql, new Object[] { Order.Status.TO_REFUND, message, userId, payment.getOrderId(), Order.Status.PAYED })) {
                        sql = "INSERT INTO SG_Refund(OrderId, PaymentId, PayType, RefundFee, ApplyTime, AddTime) VALUES (?, ?, ?, ?, NOW(), NOW())";
                        update(sql, new Object[] { payment.getOrderId(), payment.getId(), payment.getPayType(), fee });
                    }

                    return null;
                }
            });
        } catch (Exception e) {
            LOGGER.error("fail to apply refund for order: {}", payment.getOrderId(), e);
            return false;
        }

        return true;
    }

    @Override
    public long queryCountByUser(long userId, int status) {
        if (status == 1) {
            String sql = "SELECT COUNT(1) FROM SG_SubjectOrder WHERE UserId=? AND Status>0";
            return queryLong(sql, new Object[] { userId });
        } else if (status == 2) {
            String sql = "SELECT COUNT(1) FROM SG_SubjectOrder WHERE UserId=? AND Status>0 AND Status<?";
            return queryLong(sql, new Object[] { userId, Order.Status.PAYED });
        } else if (status == 3) {
            String sql = "SELECT COUNT(1) FROM SG_SubjectOrder WHERE UserId=? AND Status>=?";
            return queryLong(sql, new Object[] { userId, Order.Status.PAYED });
        }

        return 0;
    }

    @Override
    public List<Order> queryByUser(long userId, int status, int start, int count) {
        List<Long> orderIds = new ArrayList<Long>();
        if (status == 1) {
            String sql = "SELECT Id FROM SG_SubjectOrder WHERE UserId=? AND Status>0 ORDER BY AddTime DESC LIMIT ?,?";
            orderIds = queryLongList(sql, new Object[] { userId, start, count });
        } else if (status == 2) {
            String sql = "SELECT Id FROM SG_SubjectOrder WHERE UserId=? AND Status>0 AND Status<? ORDER BY AddTime DESC LIMIT ?,?";
            orderIds = queryLongList(sql, new Object[] { userId, Order.Status.PAYED, start, count });
        } else if (status == 3) {
            String sql = "SELECT Id FROM SG_SubjectOrder WHERE UserId=? AND Status>=? ORDER BY AddTime DESC LIMIT ?,?";
            orderIds = queryLongList(sql, new Object[] { userId, Order.Status.PAYED, start, count });
        }

        return list(orderIds);
    }

    @Override
    public long queryBookableCountByUserAndOrder(long userId, long orderId) {
        String sql = "SELECT COUNT(DISTINCT A.Id) FROM SG_SubjectOrderPackage A LEFT JOIN SG_SubjectOrderPackageGift B ON A.Id=B.PackageId AND B.Status<>0 WHERE A.UserId=? AND A.OrderId=? AND A.Status=1 AND A.BookableCount>0 AND (B.Id IS NULL OR (B.ToUserId=0 AND B.Deadline<=NOW()) OR B.ToUserId=?)";
        return queryLong(sql, new Object[] { userId, orderId, userId });
    }

    @Override
    public List<OrderPackage> queryBookableByUserAndOrder(long userId, long orderId, int start, int count) {
        String sql = "SELECT DISTINCT A.Id FROM SG_SubjectOrderPackage A LEFT JOIN SG_SubjectOrderPackageGift B ON A.Id=B.PackageId AND B.Status<>0 WHERE A.UserId=? AND A.OrderId=? AND A.Status=1 AND A.BookableCount>0 AND (B.Id IS NULL OR (B.ToUserId=0 AND B.Deadline<=NOW()) OR B.ToUserId=?) ORDER BY A.AddTime ASC LIMIT ?,?";
        List<Long> packageIds = queryLongList(sql, new Object[] { userId, orderId, userId, start, count });

        return listOrderPackages(packageIds);
    }

    @Override
    public long queryBookableCountByUser(long userId) {
        String sql = "SELECT COUNT(DISTINCT A.Id) FROM SG_SubjectOrderPackage A LEFT JOIN SG_SubjectOrderPackageGift B ON A.Id=B.PackageId AND B.Status<>0 WHERE A.UserId=? AND A.Status=1 AND A.BookableCount>0 AND (B.Id IS NULL OR (B.ToUserId=0 AND B.Deadline<=NOW()) OR B.ToUserId=?)";
        return queryLong(sql, new Object[] { userId, userId });
    }

    @Override
    public List<OrderPackage> queryBookableByUser(long userId, int start, int count) {
        String sql = "SELECT DISTINCT A.Id FROM SG_SubjectOrderPackage A LEFT JOIN SG_SubjectOrderPackageGift B ON A.Id=B.PackageId AND B.Status<>0 WHERE A.UserId=? AND A.Status=1 AND A.BookableCount>0 AND (B.Id IS NULL OR (B.ToUserId=0 AND B.Deadline<=NOW()) OR B.ToUserId=?) ORDER BY A.AddTime ASC LIMIT ?,?";
        List<Long> packageIds = queryLongList(sql, new Object[] { userId, userId, start, count });

        return listOrderPackages(packageIds);
    }

    @Override
    public List<OrderPackage> queryAllBookableByUser(long userId) {
        String sql = "SELECT DISTINCT A.Id FROM SG_SubjectOrderPackage A LEFT JOIN SG_SubjectOrderPackageGift B ON A.Id=B.PackageId AND B.Status<>0 WHERE A.UserId=? AND A.Status=1 AND A.BookableCount>0 AND (B.Id IS NULL OR (B.ToUserId=0 AND B.Deadline<=NOW()) OR B.ToUserId=?) ORDER BY A.AddTime ASC";
        List<Long> packageIds = queryLongList(sql, new Object[] { userId, userId });

        return listOrderPackages(packageIds);
    }

    @Override
    public Map<Long, Long> queryBookablePackageIds(Set<Long> userIds, long subjectId) {
        if (userIds.isEmpty()) return new HashMap<Long, Long>();

        Map<Long, Long> packageIdsMap = queryBookedBookablePackageIds(userIds, subjectId);

        Set<Long> notBookedUserIds = new HashSet<Long>();
        for (long userId : userIds) {
            if (!packageIdsMap.containsKey(userId)) notBookedUserIds.add(userId);
        }
        if (!notBookedUserIds.isEmpty()) {
            packageIdsMap.putAll(queryNotBookedBookablePackageIds(notBookedUserIds, subjectId));
        }

        return packageIdsMap;
    }

    private Map<Long, Long> queryBookedBookablePackageIds(Collection<Long> userIds, long subjectId) {
        final Map<Long, Long> packageIdsMap = new HashMap<Long, Long>();
        String sql = "SELECT A.UserId, A.Id AS PackageId " +
                "FROM SG_SubjectOrderPackage A " +
                "INNER JOIN SG_SubjectSku B ON A.SkuId=B.Id " +
                "INNER JOIN SG_BookedCourse C ON A.Id=C.PackageId " +
                "INNER JOIN SG_CourseSku D ON C.CourseSkuId=D.Id " +
                "WHERE A.UserId IN (" + StringUtils.join(userIds, ",") + ") AND A.BookableCount>0 AND A.Status=1 AND B.SubjectId=? AND B.Status<>0 AND C.Status<>0 AND D.Status<>0 " +
                "ORDER BY D.StartTime ASC";
        query(sql, new Object[] { subjectId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long userId = rs.getLong("userId");
                long packageId = rs.getLong("packageId");
                if (!packageIdsMap.containsKey(userId)) packageIdsMap.put(userId, packageId);
            }
        });

        return packageIdsMap;
    }

    private Map<Long, Long> queryNotBookedBookablePackageIds(Collection<Long> userIds, long subjectId) {
        final Map<Long, Long> packageIdsMap = new HashMap<Long, Long>();
        String sql = "SELECT A.UserId, A.Id AS PackageId " +
                "FROM SG_SubjectOrderPackage A " +
                "INNER JOIN SG_SubjectSku B ON A.SkuId=B.Id " +
                "LEFT JOIN SG_BookedCourse C ON A.Id=C.PackageId AND C.Status<>0 " +
                "WHERE A.UserId IN (" + StringUtils.join(userIds, ",") + ") AND A.BookableCount>0 AND A.Status=1 AND B.SubjectId=? AND B.Status<>0 AND C.Id IS NULL";
        query(sql, new Object[] { subjectId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long userId = rs.getLong("userId");
                long packageId = rs.getLong("packageId");
                if (!isGift(userId, packageId) && !packageIdsMap.containsKey(userId)) packageIdsMap.put(userId, packageId);
            }
        });

        return packageIdsMap;
    }

    @Override
    public OrderPackage getOrderPackage(long packageId) {
        Set<Long> packageIds = Sets.newHashSet(packageId);
        List<OrderPackage> packages = listOrderPackages(packageIds);

        return packages.isEmpty() ? OrderPackage.NOT_EXIST_ORDER_PACKAGE : packages.get(0);
    }

    @Override
    public Set<Integer> getOrderPackageTypes(long orderId) {
        final Set<Integer> packageTypes = new HashSet<Integer>();
        String sql = "SELECT B.CourseId, C.Type FROM SG_SubjectOrderPackage A INNER JOIN SG_SubjectSku B ON A.SkuId=B.Id INNER JOIN SG_Subject C ON B.SubjectId=C.Id WHERE A.OrderId=? AND A.Status<>0 AND B.Status<>0 AND C.Status<>0";
        query(sql, new Object[] { orderId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long courseId = rs.getLong("CourseId");
                int type = rs.getInt("Type");
                if (type == Subject.Type.TRIAL) {
                    packageTypes.add(OrderPackage.Type.TRIAL);
                } else if (courseId > 0) {
                    packageTypes.add(OrderPackage.Type.SINGLE_COURSE);
                } else {
                    packageTypes.add(OrderPackage.Type.PACKAGE);
                }
            }
        });

        return packageTypes;
    }

    @Override
    public List<OrderPackage> getOrderPackages(long orderId) {
        String sql = "SELECT Id FROM SG_SubjectOrderPackage WHERE OrderId=? AND Status<>0";
        List<Long> packageIds = queryLongList(sql, new Object[] { orderId });

        return listOrderPackages(packageIds);
    }

    @Override
    public boolean isUsed(long packageId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse WHERE PackageId=? AND Status<>0";
        return queryInt(sql, new Object[] { packageId }) > 0;
    }

    @Override
    public boolean isGift(long fromUserId, long packageId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectOrderPackageGift WHERE FromUserId=? AND PackageId=? AND ((ToUserId=0 && Deadline>NOW()) OR (ToUserId<>0 AND ToUserId<>?)) AND Status=1";
        return queryInt(sql, new Object[] { fromUserId, packageId, fromUserId }) > 0;
    }

    @Override
    public boolean isGift(long fromUserId, long toUserId, long packageId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectOrderPackageGift WHERE FromUserId=? AND ToUserId=? AND PackageId=? AND Status=1";
        return queryInt(sql, new Object[] { fromUserId, toUserId, packageId }) > 0;
    }

    @Override
    public boolean sendGift(long fromUserId, long packageId) {
        Date deadline = new Date(new Date().getTime() + 10L * 24 * 60 * 60 * 1000);
        String sql = "INSERT INTO SG_SubjectOrderPackageGift (FromUserId, ToUserId, PackageId, Deadline, AddTime) VALUES (?, 0, ?, ?, NOW())";

        return update(sql, new Object[] { fromUserId, packageId, deadline });
    }

    @Override
    public boolean isGiftFrom(long fromUserId, long packageId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectOrderPackageGift WHERE FromUserId=? AND PackageId=? AND Status=1";
        return queryInt(sql, new Object[] { fromUserId, packageId }) > 0;
    }

    @Override
    public boolean isGiftTo(long toUserId, long packageId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectOrderPackageGift WHERE ToUserId=? AND PackageId=? AND Status=1";
        return queryInt(sql, new Object[] { toUserId, packageId }) > 0;
    }

    @Override
    public boolean isGiftReceived(long packageId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectOrderPackageGift WHERE ToUserId>0 AND PackageId=? AND Status=1";
        return queryInt(sql, new Object[] { packageId }) > 0;
    }

    @Override
    public boolean isGiftExpired(long packageId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectOrderPackageGift WHERE PackageId=? AND Deadline<=NOW() AND Status=1";
        return queryInt(sql, new Object[] { packageId }) > 0;
    }

    @Override
    public boolean receiveGift(long fromUserId, long toUserId, long packageId) {
        String sql = "UPDATE SG_SubjectOrderPackageGift SET ToUserId=? WHERE FromUserId=? AND ToUserId=0 AND PackageId=? AND Deadline>NOW() AND Status<>0";
        if (!update(sql, new Object[] { toUserId, fromUserId, packageId })) return false;

        sql = "UPDATE SG_SubjectOrderPackage SET UserId=? WHERE Id=? AND UserId=? AND Status=1";
        return update(sql, new Object[] { toUserId, packageId, fromUserId });
    }

    @Override
    public boolean extendPackageTime(long packageId, int time, int timeUnit) {
        String sql = "UPDATE SG_SubjectOrderPackage SET Time=?, TimeUnit=? WHERE Id=?";
        return update(sql, new Object[] { time, timeUnit, packageId });
    }

    @Override
    public boolean prepay(long orderId) {
        String sql = "UPDATE SG_SubjectOrder SET Status=? WHERE Id=? AND (Status=? OR Status=?)";
        int updateCount = singleUpdate(sql, new Object[] { Order.Status.PRE_PAYED, orderId, Order.Status.NOT_PAYED, Order.Status.PRE_PAYED });

        return updateCount == 1;
    }

    @Override
    public boolean pay(final Payment payment) {
        try {
            execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    payOrder(payment.getOrderId());
                    enablePackage(payment.getOrderId());
                    logPayment(payment);

                    return null;
                }
            });
        } catch (Exception e) {
            LOGGER.error("fail to pay order: {}", payment.getOrderId(), e);
            return false;
        }

        return true;
    }

    private void payOrder(long orderId) {
        String sql = "UPDATE SG_SubjectOrder SET Status=? WHERE Id=? AND Status=?";
        int updateCount = singleUpdate(sql, new Object[] { Order.Status.PAYED, orderId, Order.Status.PRE_PAYED });

        if (updateCount != 1) throw new RuntimeException("fail to pay order: " + orderId);
    }

    private void enablePackage(long orderId) {
        String sql = "UPDATE SG_SubjectOrderPackage SET Status=1 WHERE OrderId=?";
        int updateCount = singleUpdate(sql, new Object[] { orderId });

        if (!(updateCount > 0)) throw new RuntimeException("fail to enable package of order: " + orderId);
    }

    private void logPayment(final Payment payment) {
        String sql = "INSERT INTO SG_SubjectPayment(OrderId, Payer, FinishTime, PayType, TradeNo, Fee, AddTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
        int updateCount = singleUpdate(sql, new Object[] { payment.getOrderId(), payment.getPayer(), payment.getFinishTime(), payment.getPayType(), payment.getTradeNo(), payment.getFee() });

        if (updateCount != 1) throw new RuntimeException("fail to log payment for order: " + payment.getOrderId());
    }

    @Override
    public boolean decreaseBookableCount(long packageId) {
        String sql = "UPDATE SG_SubjectOrderPackage SET BookableCount=BookableCount-1 WHERE Id=? AND Status=1 AND BookableCount>=1";
        return update(sql, new Object[] { packageId });
    }

    @Override
    public boolean increaseBookableCount(long packageId) {
        String sql = "UPDATE SG_SubjectOrderPackage SET BookableCount=BookableCount+1 WHERE Id=? AND Status=1 AND BookableCount<CourseCount";
        return update(sql, new Object[] { packageId });
    }

    @Override
    public boolean hasTrialOrder(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectOrder A INNER JOIN SG_Subject B ON A.SubjectId=B.Id WHERE A.UserId=? AND A.Status<>0 AND B.`Type`=?";
        return queryInt(sql, new Object[] { userId, Subject.Type.TRIAL }) > 0;
    }

    @Override
    public int getBoughtCount(long userId, long skuId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectOrder A INNER JOIN SG_SubjectOrderPackage B ON A.Id=B.OrderId WHERE A.UserId=? AND B.SkuId=? AND A.Status<>0 AND B.Status<>0";
        return queryInt(sql, new Object[] { userId, skuId });
    }

    @Override
    public Map<Long, Date> queryStartTimesOfPackages(Collection<Long> packageIds) {
        if (packageIds.isEmpty()) return new HashMap<Long, Date>();

        final Map<Long, Date> startTimesMap = new HashMap<Long, Date>();
        String sql = "SELECT A.PackageId, MIN(B.StartTime) AS StartTime FROM SG_BookedCourse A INNER JOIN SG_CourseSku B ON A.CourseSkuId=B.Id WHERE A.PackageId IN (" + StringUtils.join(packageIds, ",") + ") AND A.Status<>0 AND B.Status<>0 GROUP BY A.PackageId";
        query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                startTimesMap.put(rs.getLong("PackageId"), rs.getTimestamp("StartTime"));
            }
        });

        return startTimesMap;
    }

    @Override
    public List<Long> queryBookableUserIds() {
        String sql = "SELECT Id FROM SG_SubjectOrderPackage WHERE Status=1 AND CourseCount>1 AND BookableCount>0";
        List<Long> packageIds = queryLongList(sql);
        if (packageIds.isEmpty()) return new ArrayList<Long>();

        List<OrderPackage> packages = listOrderPackages(packageIds);
        if (packages.isEmpty()) return new ArrayList<Long>();

        Map<Long, Date> startTimesMap = queryStartTimesOfPackages(packageIds);
        Set<Long> bookablePackageIds = new HashSet<Long>();
        Date now = new Date();
        for (OrderPackage orderPackage : packages) {
            Date startTime = startTimesMap.get(orderPackage.getId());
            if (startTime == null) {
                bookablePackageIds.add(orderPackage.getId());
            } else {
                if (TimeUtil.add(startTime, orderPackage.getTime(), orderPackage.getTimeUnit()).after(now)) bookablePackageIds.add(orderPackage.getId());
            }
        }

        if (bookablePackageIds.isEmpty()) return new ArrayList<Long>();

        return listUserIdsOfPackages(bookablePackageIds);
    }

    private List<Long> listUserIdsOfPackages(Collection<Long> packageIds) {
        String sql = "SELECT DISTINCT UserId FROM SG_SubjectOrderPackage WHERE Id IN(" + StringUtils.join(packageIds, ",") + ")";
        return queryLongList(sql);
    }

    @Override
    public List<Long> queryUserIdsOfPackagesToExpired(int days) {
        try {
            String sql = "SELECT Id FROM SG_SubjectOrderPackage WHERE Status=1 AND BookableCount>0";
            List<Long> packageIds = queryLongList(sql);
            if (packageIds.isEmpty()) return new ArrayList<Long>();

            List<OrderPackage> packages = listOrderPackages(packageIds);
            if (packages.isEmpty()) return new ArrayList<Long>();

            Map<Long, Date> startTimesMap = queryStartTimesOfPackages(packageIds);
            Set<Long> packageIdsToExpired = new HashSet<Long>();
            Date now = new Date();
            Date lower = TimeUtil.SHORT_DATE_FORMAT.parse(TimeUtil.SHORT_DATE_FORMAT.format(new Date(now.getTime() + (days + 1L) * 24 * 60 * 60 * 1000)));
            Date upper = TimeUtil.SHORT_DATE_FORMAT.parse(TimeUtil.SHORT_DATE_FORMAT.format(new Date(now.getTime() + (days + 2L) * 24 * 60 * 60 * 1000)));
            for (OrderPackage orderPackage : packages) {
                Date startTime = startTimesMap.get(orderPackage.getId());
                if (startTime == null) continue;

                Date expiredTime = TimeUtil.add(startTime, orderPackage.getTime(), orderPackage.getTimeUnit());
                if ((expiredTime.equals(lower) || expiredTime.after(lower)) && expiredTime.before(upper)) packageIdsToExpired.add(orderPackage.getId());
            }

            if (packageIdsToExpired.isEmpty()) return new ArrayList<Long>();

            return listUserIdsOfPackages(packageIdsToExpired);
        } catch (ParseException e) {
            return new ArrayList<Long>();
        }
    }

    @Override
    public Payment getPayment(long orderId) {
        String sql = "SELECT Id, OrderId, Payer, FinishTime, PayType, TradeNo, Fee FROM SG_SubjectPayment WHERE OrderId=? AND Status=1";
        return queryObject(sql, new Object[] { orderId }, Payment.class, Payment.NOT_EXIST_PAYMENT);
    }

    @Override
    public Refund getRefund(long refundId) {
        String sql = "SELECT Id, OrderId, PaymentId, PayType, RefundFee, ApplyTime, FinishTime, Status FROM SG_Refund WHERE Id=? AND Status<>0";
        return queryObject(sql, new Object[] { refundId }, Refund.class, Refund.NOT_EXIST_REFUND);
    }

    @Override
    public Refund queryRefund(long orderId, long paymentId) {
        String sql = "SELECT Id, OrderId, PaymentId, PayType, RefundFee, ApplyTime, FinishTime, Status FROM SG_Refund WHERE OrderId=? AND PaymentId=? AND Status<>0";
        return queryObject(sql, new Object[] { orderId, paymentId }, Refund.class, Refund.NOT_EXIST_REFUND);
    }

    @Override
    public void refundChecked(long orderId) {
        String sql = "UPDATE SG_SubjectOrder SET Status=? WHERE Id=? AND Status=?";
        update(sql, new Object[] { Order.Status.REFUND_CHECKED, orderId, Order.Status.TO_REFUND });
    }

    @Override
    public boolean finishRefund(final Refund refund) {
        try {
            execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    String sql = "UPDATE SG_SubjectOrder SET Status=? WHERE Id=? AND (Status=? OR Status=?)";
                    update(sql, new Object[] { Order.Status.REFUNDED, refund.getOrderId(), Order.Status.TO_REFUND, Order.Status.REFUND_CHECKED });

                    sql = "UPDATE SG_Refund SET FinishTime=NOW(), Status=? WHERE Id=?";
                    update(sql, new Object[] { Refund.Status.FINISHED, refund.getId() });

                    return null;
                }
            });
            return true;
        } catch (Exception e) {
            LOGGER.error("fail to finish refund: {}", refund.getId(), e);
        }

        return false;
    }
}
