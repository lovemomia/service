package cn.momia.service.order.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.DbAccessService;
import cn.momia.service.order.Order;
import cn.momia.service.order.OrderLimitException;
import cn.momia.service.order.OrderPrice;
import cn.momia.service.order.OrderService;
import cn.momia.service.order.Payment;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.util.List;

public class OrderServiceImpl extends DbAccessService implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static final int MAX_RETRY_TIME = 10;
    private static final String[] ORDER_FIELDS = { "id", "customerId", "productId", "skuId", "prices", "contacts", "mobile", "participants", "inviteCode", "ticketNumber", "status", "addTime" };

    @Override
    public long add(final Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException
            {
                String sql = "INSERT INTO t_order(customerId, productId, skuId, prices, contacts, mobile, participants, inviteCode, ticketNumber, addTime) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, order.getCustomerId());
                ps.setLong(2, order.getProductId());
                ps.setLong(3, order.getSkuId());
                ps.setString(4, JSON.toJSONString(order.getPrices()));
                ps.setString(5, order.getContacts());
                ps.setString(6, order.getMobile());
                List<Long> participants = order.getParticipants();
                ps.setString(7, (participants == null ? "" : StringUtils.join(participants, ",")));
                ps.setString(8, order.getInviteCode());
                ps.setString(9, generateTickNumber());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    private String generateTickNumber() {
        // TODO 优化
        for (int i = 0; i < MAX_RETRY_TIME; i++) {
            long number = (long) (Math.random() * 10000000000L);
            if (!isDupTicketNumber(number)) return String.format("%010d", number);
        }

        throw new MomiaFailedException("fail to generate ticket number");
    }

    private boolean isDupTicketNumber(long number) {
        String sql = "SELECT COUNT(1) FROM t_ticket WHERE ticket=?";

        boolean isDup = jdbcTemplate.query(sql, new Object[] { number }, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getInt(1) > 0 : false;
            }
        });

        if (isDup) return true;

        sql = "INSERT INTO t_ticket(ticket) VALUES (?)";
        int count = jdbcTemplate.update(sql, new Object[] { number });

        if (count <= 0) return true;

        return false;
    }

    @Override
    public Order get(long id) {
        String sql = "SELECT " + joinFields() + " FROM t_order WHERE id=? AND status>0";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Order>() {
            @Override
            public Order extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildOrder(rs);
                return Order.NOT_EXIST_ORDER;
            }
        });
    }

    @Override
    public List<Order> list(long userId, long productId, long skuId) {
        final List<Order> orders = new ArrayList<Order>();
        String sql = "SELECT " + joinFields() + " FROM t_order WHERE customerId=? AND productId=? AND skuId=? AND status>0 ORDER BY addTime DESC";
        jdbcTemplate.query(sql, new Object[] { userId, productId, skuId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Order order = buildOrder(rs);
                if (order.exists()) orders.add(order);
            }
        });

        return orders;
    }

    private String joinFields() {
        return StringUtils.join(ORDER_FIELDS, ",");
    }

    private Order buildOrder(ResultSet rs) throws SQLException {
        try {
            Order order = new Order();

            order.setId(rs.getLong("id"));
            order.setCustomerId(rs.getLong("customerId"));
            order.setProductId(rs.getLong("productId"));
            order.setSkuId(rs.getLong("skuId"));
            order.setPrices(parseOrderPrices(order.getId(), rs.getString("prices")));
            String contacts = rs.getString("contacts");
            if (contacts == null) contacts = "";
            order.setContacts(contacts);
            order.setMobile(rs.getString("mobile"));
            order.setInviteCode(rs.getString("inviteCode"));
            order.setTicketNumber(rs.getString("ticketNumber"));
            order.setStatus(rs.getInt("status"));
            order.setAddTime(rs.getTimestamp("addTime"));

            List<Long> participants = new ArrayList<Long>();
            for (String participant : Order.PARTICIPANTS_SPLITTER.split(rs.getString("participants"))) {
                participants.add(Long.valueOf(participant));
            }
            order.setParticipants(participants);

            return order;
        } catch (Exception e) {
            LOGGER.error("fail to build order: {}", rs.getLong("id"), e);
            return Order.NOT_EXIST_ORDER;
        }
    }

    private List<OrderPrice> parseOrderPrices(long id, String priceJson) {
        List<OrderPrice> prices = new ArrayList<OrderPrice>();

        try {
            JSONArray pricesArray = JSON.parseArray(priceJson);
            for (int i = 0; i < pricesArray.size(); i++) {
                JSONObject priceObject = pricesArray.getJSONObject(i);
                prices.add(new OrderPrice(priceObject));
            }
        } catch (Exception e) {
            LOGGER.error("fail to parse order prices, order id: {}", id);
        }

        return prices;
    }

    @Override
    public long queryCountByUser(long userId, int status) {
        if (status == 1) {
            String sql = "SELECT COUNT(1) FROM t_order WHERE customerId=? AND status<>0";
            return jdbcTemplate.query(sql, new Object[] { userId }, new ResultSetExtractor<Long>() {
                @Override
                public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return rs.next() ? rs.getLong(1) : 0;
                }
            });
        } else if (status == 2) {
            String sql = "SELECT COUNT(1) FROM t_order WHERE customerId=? AND (status=? OR status=?)";
            return jdbcTemplate.query(sql, new Object[] { userId, Order.Status.NOT_PAYED, Order.Status.PRE_PAYED }, new ResultSetExtractor<Long>() {
                @Override
                public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return rs.next() ? rs.getLong(1) : 0;
                }
            });
        } else {
            String sql = "SELECT COUNT(1) FROM t_order WHERE customerId=? AND status=?";
            return jdbcTemplate.query(sql, new Object[] { userId, status }, new ResultSetExtractor<Long>() {
                @Override
                public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return rs.next() ? rs.getLong(1) : 0;
                }
            });
        }
    }

    @Override
    public List<Order> queryByUser(long userId, int status, int start, int count) {
        final List<Order> orders = new ArrayList<Order>();
        if (status == 1) {
            String sql = "SELECT " + joinFields() + " FROM t_order WHERE customerId=? AND status<>0 ORDER BY addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    Order order = buildOrder(rs);
                    if (order.exists()) orders.add(order);
                }
            });
        } else if (status == 2) {
            String sql = "SELECT " + joinFields() + " FROM t_order WHERE customerId=? AND (status=? OR status=?) ORDER BY addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, Order.Status.NOT_PAYED, Order.Status.PRE_PAYED, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    Order order = buildOrder(rs);
                    if (order.exists()) orders.add(order);
                }
            });
        } else {
            String sql = "SELECT " + joinFields() + " FROM t_order WHERE customerId=? AND status=? ORDER BY addTime DESC LIMIT ?,?";
            jdbcTemplate.query(sql, new Object[] { userId, status, start, count }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    Order order = buildOrder(rs);
                    if (order.exists()) orders.add(order);
                }
            });
        }

        return orders;
    }

    @Override
    public List<Order> queryByUserAndSku(long userId, long skuId) {
        final List<Order> orders = new ArrayList<Order>();
        String sql = "SELECT " + joinFields() + " FROM t_order WHERE customerId=? AND skuId=? AND status<>0";
        jdbcTemplate.query(sql, new Object[] { userId, skuId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Order order = buildOrder(rs);
                if (order.exists()) orders.add(order);
            }
        });

        return orders;
    }

    @Override
    public List<Order> queryAllCustomerOrderByProduct(long productId) {
        final List<Order> orders = new ArrayList<Order>();
        String sql = "SELECT " + joinFields() + " FROM t_order WHERE productId=? AND status<>0 AND status<=? ORDER BY addTime DESC";
        jdbcTemplate.query(sql, new Object[] { productId, Order.Status.FINISHED }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Order order = buildOrder(rs);
                if (order.exists()) orders.add(order);
            }
        });

        return orders;
    }

    @Override
    public List<Order> queryDistinctCustomerOrderByProduct(long productId, int start, int count) {
        final List<Order> orders = new ArrayList<Order>();
        String sql = "SELECT " + joinFields() + " FROM t_order WHERE productId=? AND status<>0 AND status<=? GROUP BY customerId ORDER BY addTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { productId, Order.Status.FINISHED, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Order order = buildOrder(rs);
                if (order.exists()) orders.add(order);
            }
        });

        return orders;
    }

    @Override
    public void checkLimit(long userId, long skuId, int count, int limit) throws OrderLimitException {
        if (limit <= 0) return;

        List<Order> orders = queryByUserAndSku(userId, skuId);
        for (Order order : orders) {
            if (!order.exists()) continue;
            count += order.getCount();
        }

        if (count > limit) throw new OrderLimitException();
    }

    @Override
    public boolean delete(long userId, long id) {
        String sql = "UPDATE t_order SET status=0 WHERE id=? AND customerId=? AND (status=? OR status=?)";
        int updateCount = jdbcTemplate.update(sql, new Object[] { id, userId, Order.Status.NOT_PAYED, Order.Status.PRE_PAYED });

        return updateCount == 1;
    }

    @Override
    public boolean prepay(long id) {
        String sql = "UPDATE t_order SET status=? WHERE id=? AND (status=? OR status=?)";
        int updateCount = jdbcTemplate.update(sql, new Object[] { Order.Status.PRE_PAYED, id, Order.Status.NOT_PAYED, Order.Status.PRE_PAYED });

        return updateCount == 1;
    }

    @Override
    public boolean pay(long id) {
        try {
            payOrder(id);
        } catch (Exception e) {
            return false;
        }

        return true;
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
        String sql = "UPDATE t_order SET status=? WHERE id=? AND status=?";
        int updateCount = jdbcTemplate.update(sql, new Object[] { Order.Status.PAYED, id, Order.Status.PRE_PAYED });

        if (updateCount != 1) throw new RuntimeException("fail to pay order: {}" + id);
    }

    private void logPayment(final Payment payment) {
        String sql = "INSERT INTO t_payment(orderId, payer, finishTime, payType, tradeNo, fee, addTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
        int updateCount = jdbcTemplate.update(sql, new Object[] { payment.getOrderId(), payment.getPayer(), payment.getFinishTime(), payment.getPayType(), payment.getTradeNo(), payment.getFee() });

        if (updateCount != 1) throw new RuntimeException("fail to log payment for order: " + payment.getOrderId());
    }

    @Override
    public boolean check(long userId, long id, long productId, long skuId) {
        String sql = "SELECT status FROM t_order WHERE id=? AND customerId=? AND productId=? AND skuId=?";
        int status = jdbcTemplate.query(sql, new Object[] { id, userId, productId, skuId }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getInt("status");
                return 0;
            }
        });

        return status >= Order.Status.PAYED;
    }

    @Override
    public List<Long> queryUserIds(long productId, long skuId) {
        final List<Long> userIds = new ArrayList<Long>();
        String sql = "SELECT customerId FROM t_order WHERE productId=? AND skuId=? AND status=?";
        jdbcTemplate.query(sql, new Object[] { productId, skuId, Order.Status.PAYED }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long userId = rs.getLong(1);
                if (userId > 0) userIds.add(userId);
            }
        });

        return userIds;
    }
}
