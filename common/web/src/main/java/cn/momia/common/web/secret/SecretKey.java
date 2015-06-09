package cn.momia.common.web.secret;

import cn.momia.common.config.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SecretKey
{
    private static String key;

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
        String sql = "SELECT `key` FROM t_secret WHERE biz=? AND status=1 ORDER BY updateTime DESC LIMIT 1";
        String biz = conf.getString("Server.Biz");
        key = jdbcTemplate.query(sql, new Object[] { biz }, new ResultSetExtractor<String>()
        {
            @Override
            public String extractData(ResultSet rs) throws SQLException, DataAccessException
            {
                if (rs.next()) return rs.getString("key");
                return null;
            }
        });

        if (StringUtils.isBlank(key)) throw new RuntimeException("secret key is empty");
    }

    public static String get()
    {
        return key;
    }
}
