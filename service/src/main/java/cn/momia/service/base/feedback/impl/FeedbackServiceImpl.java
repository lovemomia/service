package cn.momia.service.base.feedback.impl;

import cn.momia.service.base.feedback.FeedbackService;
import cn.momia.service.common.DbAccessService;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class FeedbackServiceImpl extends DbAccessService implements FeedbackService {
    @Override
    public long add(final String content, final String email, final long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_feedback(content, email, userId, addTime) VALUES(?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, content);
                ps.setString(2, email);
                ps.setLong(3, userId);

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
