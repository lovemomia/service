package cn.momia.service.course.subject.order.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.subject.order.Order;
import cn.momia.service.course.subject.order.OrderService;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderServiceImpl extends DbAccessService implements OrderService {
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
}
