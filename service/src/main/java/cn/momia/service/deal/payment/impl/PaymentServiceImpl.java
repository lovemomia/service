package cn.momia.service.deal.payment.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.PaymentService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PaymentServiceImpl extends DbAccessService implements PaymentService {
    @Override
    public long add(final Payment payment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException
            {
                String sql = "INSERT INTO t_payment(orderId, finishTime, payType, tradeNo, fee, addTime) VALUES(?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, payment.getOrderId());
                ps.setDate(2, new Date(payment.getFinishTime().getTime()));
                ps.setInt(3, payment.getPayType());
                ps.setString(4, payment.getTradeNo());
                ps.setFloat(5, payment.getFee());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Payment get(long id) {
        String sql = "SELECT id, orderId, finishTime, payType, tradeNo, fee FROM t_payment WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Payment>() {
            @Override
            public Payment extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildPayment(rs);
                return Payment.NOT_EXIST_PAYMENT;
            }
        });
    }

    private Payment buildPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();

        payment.setId(rs.getLong("id"));
        payment.setOrderId(rs.getLong("orderId"));
        payment.setFinishTime(rs.getTimestamp("finishTime"));
        payment.setPayType(rs.getInt("payType"));
        payment.setTradeNo(rs.getString("tradeNo"));
        payment.setFee(rs.getFloat("fee"));

        return payment;
    }

    @Override
    public Payment getByOrder(long orderId) {
        String sql = "SELECT id, orderId, finishTime, payType, tradeNo, fee FROM t_payment WHERE orderId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { orderId }, new ResultSetExtractor<Payment>() {
            @Override
            public Payment extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildPayment(rs);
                return Payment.NOT_EXIST_PAYMENT;
            }
        });
    }
}
