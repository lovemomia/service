package cn.momia.service.deal.order.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderServiceImpl extends DbAccessService implements OrderService {
    @Override
    public long add(final Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException
            {
                String sql = "INSERT INTO t_order(customerId, productId, skuId, price, `count`, contacts, mobile, participants, addTime) VALUES(?, ?, ?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, order.getCustomerId());
                ps.setLong(2, order.getProductId());
                ps.setLong(3, order.getSkuId());
                ps.setFloat(4, order.getPrice());
                ps.setInt(5, order.getCount());
                ps.setString(6, order.getContacts());
                ps.setString(7, order.getMobile());
                ps.setString(8, StringUtils.join(order.getParticipants(), ","));

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Order get(long id) {
        String sql = "SELECT id, customerId, productId, skuId, price, `count`, contacts, mobile, participants, status FROM t_order WHERE id=?";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Order>() {
            @Override
            public Order extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildOrder(rs);
                return Order.NOT_EXIST_ORDER;
            }
        });
    }

    private Order buildOrder(ResultSet rs) throws SQLException {
        Order order = new Order();

        order.setId(rs.getLong("id"));
        order.setCustomerId(rs.getLong("customerId"));
        order.setProductId(rs.getLong("productId"));
        order.setSkuId(rs.getLong("skuId"));
        order.setPrice(rs.getFloat("price"));
        order.setCount(rs.getInt("count"));
        order.setContacts(rs.getString("contacts"));
        order.setMobile(rs.getString("mobile"));
        order.setStatus(rs.getInt("status"));

        List<Long> participants = new ArrayList<Long>();
        for (String participant : Splitter.on(",").omitEmptyStrings().trimResults().split(rs.getString("participants")))
        {
            participants.add(Long.valueOf(participant));
        }
        order.setParticipants(participants);

        return order;
    }

    @Override
    public List<Order> queryByProduct(long productId, int start, int count) {
        String sql = "SELECT id, customerId, productId, skuId, price, `count`, contacts, mobile, participants, status FROM t_order WHERE productId=? LIMIT ?,?";
        final List<Order> orders = new ArrayList<Order>();
        jdbcTemplate.query(sql, new Object[] { productId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                orders.add(buildOrder(rs));
            }
        });

        return orders;
    }

    @Override
    public List<Integer> queryCustomerByProduct(long id, int start, int count) {
        final List<Integer> customers = new ArrayList<Integer>();

        String sql = "SELECT customerId FROM t_order WHERE productId=? LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { id, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                customers.add(rs.getInt("customerId"));
            }
        });

        return customers;
    }

    @Override
    public boolean delete(long id, long userId) {
        String sql = "UPDATE t_order SET status=0 WHERE id=? AND userId=? AND status=?";
        int updateCount = jdbcTemplate.update(sql, new Object[] { id, userId, Order.Status.NOT_PAYED });

        return updateCount == 1;
    }

    @Override
    public boolean prepay(long id) {
        String sql = "UPDATE t_order SET status=? WHERE id=? AND status=?";
        int updateCount = jdbcTemplate.update(sql, new Object[] { Order.Status.PRE_PAYED, id, Order.Status.NOT_PAYED });

        return updateCount == 1;
    }

    @Override
    public boolean pay(long id) {
        String sql = "UPDATE t_order SET status=? WHERE id=? AND status=?";
        int updateCount = jdbcTemplate.update(sql, new Object[] { Order.Status.PAYED, id, Order.Status.PRE_PAYED });

        return updateCount == 1;
    }
}
