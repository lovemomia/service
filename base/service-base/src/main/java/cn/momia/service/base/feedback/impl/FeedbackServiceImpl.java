package cn.momia.service.base.feedback.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.base.feedback.FeedbackService;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class FeedbackServiceImpl extends AbstractService implements FeedbackService {
    @Override
    public long add(final String content, final String contact) {
        KeyHolder keyHolder = insert(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_Feedback(Content, Contact, AddTime) VALUES(?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, content);
                ps.setString(2, contact);

                return ps;
            }
        });

        return keyHolder.getKey().longValue();
    }
}
