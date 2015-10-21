package cn.momia.service.course.subject.order.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
import cn.momia.service.course.subject.order.Order;
import cn.momia.service.course.subject.order.OrderService;
import cn.momia.service.course.subject.order.OrderPackage;
import cn.momia.service.course.subject.order.Payment;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderServiceImpl extends DbAccessService implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private SubjectService subjectService;

    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Override
    public long add(final Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_SubjectOrder(UserId, SubjectId, Contact, Mobile, AddTime) VALUES(?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, order.getUserId());
                ps.setLong(2, order.getSubjectId());
                ps.setString(3, order.getContact());
                ps.setString(4, order.getMobile());

                return ps;
            }
        }, keyHolder);

        long orderId = keyHolder.getKey().longValue();
        if (orderId < 0) throw new MomiaFailedException("下单失败");

        addOrderSkus(orderId, order);

        return orderId;
    }

    private void addOrderSkus(long orderId, Order order) {
        String sql = "INSERT INTO SG_SubjectOrderPackage (OrderId, SkuId, Price, `Count`, BookableCount, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
        List<Object[]> args = new ArrayList<Object[]>();
        for (SubjectSku sku : order.getSkus()) {
            int count = order.getCounts().get(sku.getId());
            if (count <= 0) continue;
            for (int i = 0; i < count; i++) {
                args.add(new Object[] { orderId, sku.getId(), sku.getPrice(), 1, sku.getCourseCount() });
            }
        }
        jdbcTemplate.batchUpdate(sql, args);
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

        String sql = "SELECT Id, UserId, SubjectId, Contact, Mobile, Status, AddTime FROM SG_SubjectOrder WHERE Id IN (" + StringUtils.join(orderIds, ",") + ") AND Status>0";
        List<Order> orders = queryList(sql, Order.class);

        Map<Long, List<OrderPackage>> orderSkusMap = queryOrderSkus(orderIds);
        Set<Long> skuIds = new HashSet<Long>();
        for (List<OrderPackage> orderPackages : orderSkusMap.values()) {
            for (OrderPackage orderPackage : orderPackages) skuIds.add(orderPackage.getSkuId());
        }
        List<SubjectSku> skus = subjectService.listSkus(skuIds);
        Map<Long, SubjectSku> skusMap = new HashMap<Long, SubjectSku>();
        for (SubjectSku sku : skus) skusMap.put(sku.getId(), sku);

        for (Order order : orders) {
            List<OrderPackage> orderPackages = orderSkusMap.get(order.getId());
            List<SubjectSku> subjectSkus = new ArrayList<SubjectSku>();
            Map<Long, Integer> counts = new HashMap<Long, Integer>();
            for (OrderPackage orderPackage : orderPackages) {
                SubjectSku subjectSku = skusMap.get(orderPackage.getSkuId()).clone();
                subjectSku.setPrice(orderPackage.getPrice());
                subjectSkus.add(subjectSku);

                counts.put(orderPackage.getSkuId(), orderPackage.getCount());
            }

            order.setSkus(subjectSkus);
            order.setCounts(counts);
        }

        return orders;
    }

    private Map<Long, List<OrderPackage>> queryOrderSkus(Collection<Long> orderIds) {
        if (orderIds.isEmpty()) return new HashMap<Long, List<OrderPackage>>();

        String sql = "SELECT Id FROM SG_SubjectOrderPackage WHERE OrderId IN (" + StringUtils.join(orderIds, ",") + ") AND Status=1";
        List<Long> orderSkuIds = queryLongList(sql);
        List<OrderPackage> orderPackages = listOrderSkus(orderSkuIds);

        Map<Long, List<OrderPackage>> orderSkusMap = new HashMap<Long, List<OrderPackage>>();
        for (long orderId : orderIds) orderSkusMap.put(orderId, new ArrayList<OrderPackage>());
        for (OrderPackage orderPackage : orderPackages) orderSkusMap.get(orderPackage.getOrderId()).add(orderPackage);

        return orderSkusMap;
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
    public long queryBookableCountByUser(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectOrder A INNER JOIN SG_SubjectOrderPackage B ON A.Id=B.OrderId WHERE A.UserId=? AND A.Status>=? AND B.Status=1 AND B.BookableCount>0";
        return queryLong(sql, new Object[] { userId, Order.Status.PAYED });
    }

    @Override
    public List<OrderPackage> queryBookableByUser(long userId, int start, int count) {
        String sql = "SELECT B.Id FROM SG_SubjectOrder A INNER JOIN SG_SubjectOrderPackage B ON A.Id=B.OrderId WHERE A.UserId=? AND A.Status>=? AND B.Status=1 AND B.BookableCount>0 ORDER BY B.AddTime ASC LIMIT ?,?";
        List<Long> orderSkuIds = queryLongList(sql, new Object[] { userId, Order.Status.PAYED, start, count });

        return listOrderSkus(orderSkuIds);
    }

    private List<OrderPackage> listOrderSkus(List<Long> orderSkuIds) {
        if (orderSkuIds.isEmpty()) return new ArrayList<OrderPackage>();

        String sql = "SELECT Id, OrderId, SkuId, Price, Count, BookableCount FROM SG_SubjectOrderPackage WHERE Id IN (" + StringUtils.join(orderSkuIds, ",") + ") AND Status=1";
        List<OrderPackage> orderPackages = queryList(sql, OrderPackage.class);

        return orderPackages;
    }

    @Override
    public Map<Long, Date> queryStartTimesByPackages(Set<Long> packageIds) {
        if (packageIds.isEmpty()) return new HashMap<Long, Date>();

        final Map<Long, Date> startTimesMap = new HashMap<Long, Date>();
        String sql = "SELECT PackageId, MIN(StartTime) AS StartTime FROM SG_BookedCourse WHERE PackageId IN (" + StringUtils.join(packageIds, ",") + ") AND Status=1 GROUP BY PackageId";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                startTimesMap.put(rs.getLong("PackageId"), rs.getTimestamp("StartTime"));
            }
        });

        return startTimesMap;
    }

    @Override
    public boolean prepay(long orderId) {
        String sql = "UPDATE SG_SubjectOrder SET Status=? WHERE Id=? AND (Status=? OR Status=?)";
        int updateCount = jdbcTemplate.update(sql, new Object[] { Order.Status.PRE_PAYED, orderId, Order.Status.NOT_PAYED, Order.Status.PRE_PAYED });

        return updateCount == 1;
    }

    @Override
    public boolean pay(final Payment payment) {
        try {
            transactionTemplate.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    payOrder(payment.getOrderId());
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
        int updateCount = jdbcTemplate.update(sql, new Object[] { Order.Status.PAYED, orderId, Order.Status.PRE_PAYED });

        if (updateCount != 1) throw new RuntimeException("fail to pay order: {}" + orderId);
    }

    private void logPayment(final Payment payment) {
        String sql = "INSERT INTO SG_SubjectPayment(OrderId, Payer, FinishTime, PayType, TradeNo, Fee, AddTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
        int updateCount = jdbcTemplate.update(sql, new Object[] { payment.getOrderId(), payment.getPayer(), payment.getFinishTime(), payment.getPayType(), payment.getTradeNo(), payment.getFee() });

        if (updateCount != 1) throw new RuntimeException("fail to log payment for order: " + payment.getOrderId());
    }
}
