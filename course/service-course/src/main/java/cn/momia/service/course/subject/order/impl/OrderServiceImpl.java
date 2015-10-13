package cn.momia.service.course.subject.order.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.subject.order.Order;
import cn.momia.service.course.subject.order.OrderContact;
import cn.momia.service.course.subject.order.OrderService;
import cn.momia.service.course.subject.order.Payment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderServiceImpl extends DbAccessService implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static final String[] ORDER_FIELDS = { "Id", "UserId", "SubjectId", "SkuId", "Price", "Count", "Contact", "Mobile", "Status", "AddTime" };

    @Override
    public long add(final Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException
            {
                String sql = "INSERT INTO SG_SubjectOrder(UserId, SubjectId, SkuId, Price, `Count`, Contact, Mobile, AddTime) VALUES(?, ?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, order.getUserId());
                ps.setLong(2, order.getSubjectId());
                ps.setLong(3, order.getSkuId());
                ps.setBigDecimal(4, order.getPrice());
                ps.setInt(5, order.getCount());
                ps.setString(6, order.getContact().getName());
                ps.setString(7, order.getContact().getMobile());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();    }

    @Override
    public Order get(long id) {
        String sql = "SELECT " + joinFields() + " FROM SG_SubjectOrder WHERE Id=? AND Status>0";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Order>() {
            @Override
            public Order extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? buildOrder(rs) : Order.NOT_EXIST_ORDER;
            }
        });
    }

    private String joinFields() {
        return StringUtils.join(ORDER_FIELDS, ",");
    }

    private Order buildOrder(ResultSet rs) {
        try {
            Order order = new Order();

            order.setId(rs.getLong("Id"));
            order.setUserId(rs.getLong("UserId"));
            order.setSubjectId(rs.getLong("SubjectId"));
            order.setSkuId(rs.getLong("SkuId"));
            order.setPrice(rs.getBigDecimal("Price"));
            order.setCount(rs.getInt("Count"));

            OrderContact contact = new OrderContact();
            contact.setName(rs.getString("Contact"));
            contact.setMobile(rs.getString("Mobile"));
            order.setContact(contact);

            order.setStatus(rs.getInt("status"));
            order.setAddTime(rs.getTimestamp("addTime"));

            return order;
        } catch (Exception e) {
            return Order.NOT_EXIST_ORDER;
        }
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
