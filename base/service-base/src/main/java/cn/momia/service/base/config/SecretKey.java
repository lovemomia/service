package cn.momia.service.base.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SecretKey
{
    private String biz;
    private JdbcTemplate jdbcTemplate;

    private Map<String, String> keys = new HashMap<String, String>();

    public void setBiz(String biz) {
        this.biz = biz;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init()
    {
        String sql = "SELECT biz, `key` FROM t_secret WHERE status=1 ORDER BY updateTime ASC";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                keys.put(rs.getString("biz"), rs.getString("key"));
            }
        });

        if (StringUtils.isBlank(get())) throw new RuntimeException("secret key is empty");
        if (StringUtils.isBlank(getPasswordSecretKey())) throw new RuntimeException("password secret key is empty");
    }

    public String get()
    {
        return keys.get(biz);
    }

    public String get(String biz) {
        return keys.get(biz);
    }

    public String getPasswordSecretKey() {
        return keys.get("password");
    }
}
