package cn.momia.service.course.subject.order.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
import cn.momia.service.course.subject.order.Order;
import cn.momia.service.course.subject.order.OrderContact;
import cn.momia.service.course.subject.order.OrderService;
import cn.momia.service.course.subject.order.OrderSku;
import cn.momia.service.course.subject.order.Payment;
import com.google.common.base.Function;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    private static final String[] ORDER_FIELDS = { "Id", "UserId", "SubjectId", "Contact", "Mobile", "Status", "AddTime" };
    private static final String[] ORDER_SKU_FIELDS = { "Id", "OrderId", "SkuId", "Price", "Count", "BookableCount" };

    private SubjectService subjectService;

    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    private Function<ResultSet, Order> orderFunc = new Function<ResultSet, Order>() {
        @Override
        public Order apply(ResultSet rs) {
            try {
                Order order = new Order();

                order.setId(rs.getLong("Id"));
                order.setUserId(rs.getLong("UserId"));
                order.setSubjectId(rs.getLong("SubjectId"));

                OrderContact contact = new OrderContact();
                contact.setName(rs.getString("Contact"));
                contact.setMobile(rs.getString("Mobile"));
                order.setContact(contact);

                order.setStatus(rs.getInt("Status"));
                order.setAddTime(rs.getTimestamp("AddTime"));

                return order;
            } catch (Exception e) {
                return Order.NOT_EXIST_ORDER;
            }
        }
    };

    private Function<ResultSet, OrderSku> orderSkuFunc = new Function<ResultSet, OrderSku>() {
        @Override
        public OrderSku apply(ResultSet rs) {
            try {
                OrderSku orderSku = new OrderSku();

                orderSku.setId(rs.getLong("Id"));
                orderSku.setOrderId(rs.getLong("OrderId"));
                orderSku.setSkuId(rs.getLong("SkuId"));
                orderSku.setPrice(rs.getBigDecimal("Price"));
                orderSku.setCount(rs.getInt("Count"));
                orderSku.setBookableCount(rs.getInt("BookableCount"));

                return orderSku;
            } catch (Exception e) {
                return OrderSku.NOT_EXIST_ORDER_SKU;
            }
        }
    };

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
        String sql = "INSERT INTO SG_SubjectOrderSku (OrderId, SubjectId, SkuId, Price, `Count`, BookableCount, AddTime) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        List<Object[]> args = new ArrayList<Object[]>();
        for (SubjectSku sku : order.getSkus()) {
            int count = order.getCounts().get(sku.getId());
            if (count <= 0) continue;
            args.add(new Object[] { orderId, order.getSubjectId(), sku.getId(), sku.getPrice(), count, sku.getCourseCount() * count  });
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

        List<Order> orders = new ArrayList<Order>();
        String sql = "SELECT " + joinFields() + " FROM SG_SubjectOrder WHERE Id IN (" + StringUtils.join(ids, ",") + ") AND Status>0";
        jdbcTemplate.query(sql, new ListResultSetExtractor<Order>(orders, orderFunc));

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

    private String joinFields() {
        return StringUtils.join(ORDER_FIELDS, ",");
    }

    private Map<Long, List<OrderSku>> queryOrderSkus(Collection<Long> ids) {
        if (ids.isEmpty()) return new HashMap<Long, List<OrderSku>>();

        List<OrderSku> orderSkus = new ArrayList<OrderSku>();
        String sql = "SELECT " + joinSkuFields() + " FROM SG_SubjectOrderSku WHERE OrderId IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new ListResultSetExtractor<OrderSku>(orderSkus, orderSkuFunc));

        Map<Long, List<OrderSku>> orderSkusMap = new HashMap<Long, List<OrderSku>>();
        for (long id : ids) orderSkusMap.put(id, new ArrayList<OrderSku>());
        for (OrderSku orderSku : orderSkus) orderSkusMap.get(orderSku.getOrderId()).add(orderSku);

        return orderSkusMap;
    }

    private String joinSkuFields() {
        return StringUtils.join(ORDER_SKU_FIELDS, ",");
    }

    @Override
    public long queryCountByUser(long userId, int status) {
        if (status == 1) {
            String sql = "SELECT COUNT(1) FROM SG_SubjectOrder WHERE UserId=? AND Status>0";
            return jdbcTemplate.queryForObject(sql, new Object[] { userId }, Long.class);
        } else if (status == 2) {
            String sql = "SELECT COUNT(1) FROM SG_SubjectOrder WHERE UserId=? AND Status>0 AND Status<?";
            return jdbcTemplate.queryForObject(sql, new Object[] { userId, Order.Status.PAYED }, Long.class);
        } else if (status == 3) {
            String sql = "SELECT COUNT(1) FROM SG_SubjectOrder WHERE UserId=? AND Status>=?";
            return jdbcTemplate.queryForObject(sql, new Object[] { userId, Order.Status.PAYED }, Long.class);
        }

        return 0;
    }

    @Override
    public List<Order> queryByUser(long userId, int status, int start, int count) {
        List<Long> ids = new ArrayList<Long>();
        if (status == 1) {
            String sql = "SELECT Id FROM SG_SubjectOrder WHERE UserId=? AND Status>0 ORDER BY AddTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, start, count }, new LongListResultSetExtractor(ids));
        } else if (status == 2) {
            String sql = "SELECT Id FROM SG_SubjectOrder WHERE UserId=? AND Status>0 AND Status<? ORDER BY AddTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, Order.Status.PAYED, start, count }, new LongListResultSetExtractor(ids));
        } else if (status == 3) {
            String sql = "SELECT Id FROM SG_SubjectOrder WHERE UserId=? AND Status>=? ORDER BY AddTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, Order.Status.PAYED, start, count }, new LongListResultSetExtractor(ids));
        }

        return list(ids);
    }

    @Override
    public long queryBookableCountByUser(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_SubjectOrder A INNER JOIN SG_SubjectOrderSku B ON A.Id=B.OrderId WHERE A.UserId=? AND A.Status>=? AND B.Status=1 AND B.BookableCount>0";
        return jdbcTemplate.queryForObject(sql, new Object[] { userId, Order.Status.PAYED }, Long.class);
    }

    @Override
    public List<OrderSku> queryBookableByUser(long userId, int start, int count) {
        String sql = "SELECT B.Id FROM SG_SubjectOrder A INNER JOIN SG_SubjectOrderSku B ON A.Id=B.OrderId WHERE A.UserId=? AND A.Status>=? AND B.Status=1 AND B.BookableCount>0 ORDER BY B.AddTime ASC LIMIT ?,?";
        List<Long> orderSkuIds = jdbcTemplate.queryForList(sql, new Object[] { userId, Order.Status.PAYED, start, count }, Long.class);

        return listOrderSkus(orderSkuIds);
    }

    private List<OrderSku> listOrderSkus(List<Long> orderSkuIds) {
        if (orderSkuIds.isEmpty()) return new ArrayList<OrderSku>();

        List<OrderSku> orderSkus = new ArrayList<OrderSku>();
        String sql = "SELECT " + joinSkuFields() + " FROM SG_SubjectOrderSku WHERE Id IN (" + StringUtils.join(orderSkuIds, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new ListResultSetExtractor<OrderSku>(orderSkus, orderSkuFunc));

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
