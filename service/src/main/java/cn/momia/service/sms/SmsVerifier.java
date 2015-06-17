package cn.momia.service.sms;

import cn.momia.service.base.DbAccessService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SmsVerifier extends DbAccessService {
    public boolean verify(String mobile, String code) {
        String sql = "SELECT generateTime FROM t_verify WHERE phone=? AND code=? AND status=1 AND ";

        return jdbcTemplate.query(sql, new Object[] { mobile, code }, new ResultSetExtractor<Boolean>()
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
    }
}
