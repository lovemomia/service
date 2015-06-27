package cn.momia.service.common;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class DbAccessService {
    protected JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
