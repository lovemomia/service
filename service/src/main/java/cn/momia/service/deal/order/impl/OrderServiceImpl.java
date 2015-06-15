package cn.momia.service.deal.order.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.user.User;
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

    public User buildUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setMobile(rs.getString("mobile"));
        user.setTypes(rs.getInt("types"));
        user.setName(rs.getString("name"));
        user.setDesc(rs.getString("desc"));
        user.setToken(rs.getString("token"));
        user.setSex(rs.getInt("sex"));
        user.setAddress(rs.getString("address"));
        user.setIdCardNo(rs.getString("idCardNo"));
        user.setIdCardPic(rs.getString("idCardPic"));
        user.setAddTime(rs.getTimestamp("addTime"));
        user.setUpdateTime(rs.getTimestamp("updateTime"));
        return user;
    }
    @Override
    public List<User> queryUserByProduct(long productId, int start, int count) {
        String sql = "select id,mobile,types,name,`desc`,token,sex,address,idCardNo,idCardPic,addTime,updateTime from t_user where id in (" +
                "select customerId from t_order where productId=?) limit ?,?";
        final List<User> users = new ArrayList<User>();
        jdbcTemplate.query(sql, new Object[]{ productId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                users.add(buildUser(rs));
            }
        });
        return users;
    }

    @Override
    public boolean pay(long id) {
        String sql = "UPDATE t_order SET status=? WHERE id=?";
        int updateCount = jdbcTemplate.update(sql, new Object[] { Order.Status.PAYED, id });

        return updateCount > 0;
    }
}
