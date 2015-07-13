package cn.momia.service.sms;

import cn.momia.service.common.DbAccessService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SmsVerifier extends DbAccessService {
    public boolean verify(String mobile, String code) {
        String sql = "SELECT generateTime FROM t_verify WHERE mobile=? AND code=? AND status=1";
        boolean successful = jdbcTemplate.query(sql, new Object[] { mobile, code }, new ResultSetExtractor<Boolean>()
        {
            @Override
            public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException
            {
                if (rs.next()) {
                    Date generateTime = rs.getTimestamp("generateTime");
                    return new Date().getTime() - generateTime.getTime() < 30 * 60 * 1000;
                }

                return false;
            }
        });
        if (successful) disable(mobile, code);

        return successful;
    }

    private void disable(String mobile, String code) {
        String sql = "UPDATE t_verify SET status=0 WHERE mobile=? AND code=?";
        jdbcTemplate.update(sql, new Object[] { mobile, code });
    }
}
