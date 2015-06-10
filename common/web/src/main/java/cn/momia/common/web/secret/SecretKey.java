package cn.momia.common.web.secret;

import cn.momia.common.config.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SecretKey
{
    private static String key;
    private static String alipayPrivateKey;
    private static String alipayPublicKey;

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
        String sql = "SELECT `key`, alipayPrivateKey, alipayPublicKey FROM t_secret WHERE biz=? AND status=1 ORDER BY updateTime DESC LIMIT 1";
        String biz = conf.getString("Server.Biz");
        jdbcTemplate.query(sql, new Object[] { biz }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                key = rs.getString("key");
                alipayPrivateKey = rs.getString("alipayPrivateKey");
                alipayPublicKey = rs.getString("alipayPublicKey");
            }
        });

        if (StringUtils.isBlank(key)) throw new RuntimeException("secret key is empty");
        if (StringUtils.isBlank(alipayPrivateKey)) throw new RuntimeException("alipay private key is empty");
        if (StringUtils.isBlank(alipayPublicKey)) throw new RuntimeException("alipay public key is empty");
    }

    public static String get()
    {
        return key;
    }

    public static String getAlipayPrivateKey() {
        return alipayPrivateKey;
    }

    public static String getAlipayPublicKey() {
        return alipayPublicKey;
    }
}
