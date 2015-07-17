package cn.momia.service.common.feedback.impl;

import cn.momia.service.common.feedback.FeedbackService;
import cn.momia.service.base.DbAccessService;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class FeedbackServiceImpl extends DbAccessService implements FeedbackService {
    @Override
    public long add(final String content, final String email) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_feedback(content, email, addTime) VALUES(?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, content);
                ps.setString(2, email);

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
