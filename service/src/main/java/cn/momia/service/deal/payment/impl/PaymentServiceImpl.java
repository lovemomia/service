package cn.momia.service.deal.payment.impl;

import cn.momia.service.common.DbAccessService;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class PaymentServiceImpl extends DbAccessService implements PaymentService {
    private static final String[] PAYMENT_FIELDS = { "id", "orderId", "payer", "finishTime", "payType", "tradeNo", "fee" };
    @Override
    public long add(final Payment payment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException
            {
                String sql = "INSERT INTO t_payment(orderId, payer, finishTime, payType, tradeNo, fee, addTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, payment.getOrderId());
                ps.setString(2, payment.getPayer());
                ps.setTimestamp(3, new Timestamp(payment.getFinishTime().getTime()));
                ps.setInt(4, payment.getPayType());
                ps.setString(5, payment.getTradeNo());
                ps.setBigDecimal(6, payment.getFee());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Payment get(long id) {
        String sql = "SELECT " + joinFields() + " FROM t_payment WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Payment>() {
            @Override
            public Payment extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildPayment(rs);
                return Payment.NOT_EXIST_PAYMENT;
            }
        });
    }

    private String joinFields() {
        return StringUtils.join(PAYMENT_FIELDS, ",");
    }

    private Payment buildPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();

        payment.setId(rs.getLong("id"));
        payment.setOrderId(rs.getLong("orderId"));
        payment.setPayer(rs.getString("payer"));
        payment.setFinishTime(rs.getTimestamp("finishTime"));
        payment.setPayType(rs.getInt("payType"));
        payment.setTradeNo(rs.getString("tradeNo"));
        payment.setFee(rs.getBigDecimal("fee"));

        return payment;
    }

    @Override
    public Payment getByOrder(long orderId) {
        String sql = "SELECT " + joinFields() + " FROM t_payment WHERE orderId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { orderId }, new ResultSetExtractor<Payment>() {
            @Override
            public Payment extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildPayment(rs);
                return Payment.NOT_EXIST_PAYMENT;
            }
        });
    }
}
