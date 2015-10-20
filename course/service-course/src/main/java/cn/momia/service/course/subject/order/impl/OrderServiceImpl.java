package cn.momia.service.course.subject.order.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
import cn.momia.service.course.subject.order.Order;
import cn.momia.service.course.subject.order.OrderService;
import cn.momia.service.course.subject.order.OrderSku;
import cn.momia.service.course.subject.order.Payment;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
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
                ps.setString(3, order.getContact().getName());
                ps.setString(4, order.getContact().getMobile());

                return ps;
            }
        }, keyHolder);

        long orderId = keyHolder.getKey().longValue();
        if (orderId < 0) throw new MomiaFailedException("下单失败");

        addOrderSkus(orderId, order);

        return orderId;
    }

    private void addOrderSkus(long orderId, Order order) {
        String sql = "INSERT INTO SG_SubjectOrderSku (OrderId, SkuId, Price, `Count`, BookableCount, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
        List<Object[]> args = new ArrayList<Object[]>();
        for (SubjectSku sku : order.getSkus()) {
            int count = order.getCounts().get(sku.getId());
            if (count <= 0) continue;
            args.add(new Object[] { orderId, sku.getId(), sku.getPrice(), count, sku.getCourseCount() * count  });
        }
        jdbcTemplate.batchUpdate(sql, args);
    }

    @Override
    public Order get(long id) {
        Set<Long> ids = Sets.newHashSet(id);
        List<Order> orders = list(ids);

        return orders.isEmpty() ? Order.NOT_EXIST_ORDER : orders.get(0);
    }

    @Override
    public List<Order> list(Collection<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<Order>();

        String sql = "SELECT * FROM SG_SubjectOrder WHERE Id IN (" + StringUtils.join(ids, ",") + ") AND Status>0";
        List<Order> orders = queryList(sql, Order.class);

        Map<Long, List<OrderSku>> orderSkusMap = queryOrderSkus(ids);
        Set<Long> skuIds = new HashSet<Long>();
        for (List<OrderSku> orderSkus : orderSkusMap.values()) {
            for (OrderSku orderSku : orderSkus) skuIds.add(orderSku.getSkuId());
        }
        List<SubjectSku> skus = subjectService.listSkus(skuIds);
        Map<Long, SubjectSku> skusMap = new HashMap<Long, SubjectSku>();
        for (SubjectSku sku : skus) skusMap.put(sku.getId(), sku);

        for (Order order : orders) {
            List<OrderSku> orderSkus = orderSkusMap.get(order.getId());
            List<SubjectSku> subjectSkus = new ArrayList<SubjectSku>();
            Map<Long, Integer> counts = new HashMap<Long, Integer>();
            for (OrderSku orderSku : orderSkus) {
                SubjectSku subjectSku = skusMap.get(orderSku.getSkuId()).clone();
                subjectSku.setPrice(orderSku.getPrice());
                subjectSkus.add(subjectSku);

                counts.put(orderSku.getSkuId(), orderSku.getCount());
            }

            order.setSkus(subjectSkus);
            order.setCounts(counts);
        }

        return orders;
    }

    private Map<Long, List<OrderSku>> queryOrderSkus(Collection<Long> ids) {
        if (ids.isEmpty()) return new HashMap<Long, List<OrderSku>>();

        String sql = "SELECT * FROM SG_SubjectOrderSku WHERE OrderId IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        List<OrderSku> orderSkus = queryList(sql, OrderSku.class);

        Map<Long, List<OrderSku>> orderSkusMap = new HashMap<Long, List<OrderSku>>();
        for (long id : ids) orderSkusMap.put(id, new ArrayList<OrderSku>());
        for (OrderSku orderSku : orderSkus) orderSkusMap.get(orderSku.getOrderId()).add(orderSku);

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
        List<Long> ids = new ArrayList<Long>();
        if (status == 1) {
            String sql = "SELECT Id FROM SG_SubjectOrder WHERE UserId=? AND Status>0 ORDER BY AddTime DESC LIMIT ?,?";
            ids = queryLongList(sql, new Object[] { userId, start, count });
        } else if (status == 2) {
            String sql = "SELECT Id FROM SG_SubjectOrder WHERE UserId=? AND Status>0 AND Status<? ORDER BY AddTime DESC LIMIT ?,?";
            ids = queryLongList(sql, new Object[] { userId, Order.Status.PAYED, start, count });
        } else if (status == 3) {
            String sql = "SELECT Id FROM SG_SubjectOrder WHERE UserId=? AND Status>=? ORDER BY AddTime DESC LIMIT ?,?";
            ids = queryLongList(sql, new Object[] { userId, Order.Status.PAYED, start, count });
        }

        return list(ids);
    }

    @Override
    public long queryBookableCountByUser(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectOrder A INNER JOIN SG_SubjectOrderSku B ON A.Id=B.OrderId WHERE A.UserId=? AND A.Status>=? AND B.Status=1 AND B.BookableCount>0";
        return queryLong(sql, new Object[] { userId, Order.Status.PAYED });
    }

    @Override
    public List<OrderSku> queryBookableByUser(long userId, int start, int count) {
        String sql = "SELECT B.Id FROM SG_SubjectOrder A INNER JOIN SG_SubjectOrderSku B ON A.Id=B.OrderId WHERE A.UserId=? AND A.Status>=? AND B.Status=1 AND B.BookableCount>0 ORDER BY B.AddTime ASC LIMIT ?,?";
        List<Long> orderSkuIds = queryLongList(sql, new Object[] { userId, Order.Status.PAYED, start, count });

        return listOrderSkus(orderSkuIds);
    }

    private List<OrderSku> listOrderSkus(List<Long> orderSkuIds) {
        if (orderSkuIds.isEmpty()) return new ArrayList<OrderSku>();

        String sql = "SELECT * FROM SG_SubjectOrderSku WHERE Id IN (" + StringUtils.join(orderSkuIds, ",") + ") AND Status=1";
        List<OrderSku> orderSkus = queryList(sql, OrderSku.class);

        return orderSkus;
    }

    @Override
    public boolean prepay(long id) {
        String sql = "UPDATE SG_SubjectOrder SET Status=? WHERE Id=? AND (Status=? OR Status=?)";
        int updateCount = jdbcTemplate.update(sql, new Object[] { Order.Status.PRE_PAYED, id, Order.Status.NOT_PAYED, Order.Status.PRE_PAYED });

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

    private void payOrder(long id) {
        String sql = "UPDATE SG_SubjectOrder SET Status=? WHERE Id=? AND Status=?";
        int updateCount = jdbcTemplate.update(sql, new Object[] { Order.Status.PAYED, id, Order.Status.PRE_PAYED });

        if (updateCount != 1) throw new RuntimeException("fail to pay order: {}" + id);
    }

    private void logPayment(final Payment payment) {
        String sql = "INSERT INTO SG_SubjectPayment(OrderId, Payer, FinishTime, PayType, TradeNo, Fee, AddTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
        int updateCount = jdbcTemplate.update(sql, new Object[] { payment.getOrderId(), payment.getPayer(), payment.getFinishTime(), payment.getPayType(), payment.getTradeNo(), payment.getFee() });

        if (updateCount != 1) throw new RuntimeException("fail to log payment for order: " + payment.getOrderId());
    }
}
