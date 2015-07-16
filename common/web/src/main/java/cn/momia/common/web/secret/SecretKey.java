package cn.momia.common.web.secret;

import cn.momia.common.config.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SecretKey
{
    private static String biz;
    private static Map<String, String> keys = new HashMap<String, String>();

    private Configuration conf;
    private JdbcTemplate jdbcTemplate;

    public void setConf(Configuration conf)
    {
        this.conf = conf;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init()
    {
        biz = conf.getString("Server.Biz");

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

    public static String get()
    {
        return keys.get(biz);
    }

    public static String get(String biz) {
        return keys.get(biz);
    }

    public static String getPasswordSecretKey() {
        return keys.get("password");
    }
}
