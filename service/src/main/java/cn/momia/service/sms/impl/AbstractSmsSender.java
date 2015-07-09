package cn.momia.service.sms.impl;

import cn.momia.common.config.Configuration;
import cn.momia.service.common.DbAccessService;
import cn.momia.service.sms.SmsSender;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSmsSender extends DbAccessService implements SmsSender {
    private ExecutorService executorService;

    protected Configuration conf;

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public void init() {
        int corePoolSize = conf.getInt("Sms.CorePoolSize");
        int maxPoolSize = conf.getInt("Sms.MaxPoolSize");
        int queueSize = conf.getInt("Sms.QueueSize");
        executorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
    }

    @Override
    public void send(String mobile, String type) throws SmsLoginException {
        if(StringUtils.equals(type, "login") && !userExists(mobile)) throw new SmsLoginException("用户不存在，请先注册");

        String code = getGeneratedCode(mobile);
        if (StringUtils.isBlank(code)) {
            boolean outOfDate = (code != null); // null 表示没有生成过，空表示生成过，但已过期
            code = generateCode(mobile);
            updateCode(mobile, code, outOfDate);
        }
        Date lastSendTime = getLastSendTime(mobile);
        if (lastSendTime != null && new Date().getTime() - lastSendTime.getTime() < 60 * 1000) return;

        sendAsync(mobile, code);

    }

    private boolean userExists(String mobile) {
        String sql = "select id from t_user where mobile=?";

        return jdbcTemplate.query(sql, new Object[] { mobile }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        }) > 0;
    }

    private String getGeneratedCode(String mobile)
    {
        String sql = "SELECT code, status, generateTime FROM t_verify WHERE mobile=?";

        return jdbcTemplate.query(sql, new Object[] { mobile }, new ResultSetExtractor<String>()
        {
            @Override
            public String extractData(ResultSet rs) throws SQLException, DataAccessException
            {
                if (!rs.next()) return null;

                String code = rs.getString("code");
                int status = rs.getInt("status");
                Date generateTime = rs.getTimestamp("generateTime");

                if (status == 0 || generateTime == null || new Date().getTime() - generateTime.getTime() > 30 * 60 * 1000) return "";
                return code;
            }
        });
    }

    private String generateCode(String mobile)
    {
        int number = (int) (Math.random() * 1000000);

        return String.format("%06d", number);
    }

    private void updateCode(String mobile, String code, boolean outOfDate)
    {
        if (outOfDate)
        {
            String sql = "UPDATE t_verify SET mobile=?, code=?, generateTime=NOW(), sendTime=NULL, status=1 WHERE mobile=?";
            jdbcTemplate.update(sql, new Object[] { mobile, code, mobile });
        }
        else
        {
            String sql = "INSERT INTO t_verify(mobile, code, generateTime) VALUES (?, ?, NOW())";
            jdbcTemplate.update(sql, new Object[] { mobile, code });
        }
    }

    private Date getLastSendTime(String mobile)
    {
        String sql = "SELECT sendTime FROM t_verify WHERE mobile=?";

        return jdbcTemplate.query(sql, new Object[] { mobile }, new ResultSetExtractor<Date>()
        {
            @Override
            public Date extractData(ResultSet rs) throws SQLException, DataAccessException
            {
                if (rs.next()) return rs.getTimestamp("sendTime");
                return null;
            }
        });
    }

    private void sendAsync(final String mobile, final String code) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (doSend(mobile, code)) {
                    updateSendTime(mobile);
                }
            }
        });
    }

    protected abstract boolean doSend(String mobile, String code);

    private boolean updateSendTime(String mobile)
    {
        String sql = "UPDATE t_verify SET sendTime=NOW() WHERE mobile=?";

        return jdbcTemplate.update(sql, new Object[] { mobile }) == 1;
    }
}
