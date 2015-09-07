package cn.momia.service.base.recommend.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.base.recommend.RecommendService;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class RecommendServiceImpl extends DbAccessService implements RecommendService {
    @Override
    public long add(final String content, final String time, final String address, final String contacts) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_recommend(content, time, address, contacts, addTime) VALUES(?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, content);
                ps.setString(2, time);
                ps.setString(3, address);
                ps.setString(4, contacts);

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
