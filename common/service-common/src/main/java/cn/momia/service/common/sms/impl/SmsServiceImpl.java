package cn.momia.service.common.sms.impl;

import cn.momia.api.base.exception.MomiaFailedException;
import cn.momia.service.base.config.Configuration;
import cn.momia.service.base.impl.DbAccessService;
import cn.momia.service.common.sms.SmsSender;
import cn.momia.service.common.sms.SmsService;
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

public class SmsServiceImpl extends DbAccessService implements SmsService {
    private ExecutorService executorService;
    private SmsSender sender;

    public void setSender(SmsSender sender) {
        this.sender = sender;
    }

    @Override
    public boolean sendCode(String mobile, String type) {
//        if (StringUtils.equals(type, "register") && userExists(mobile)) throw new MomiaFailedException("该手机号已经注册过");
//        else 
        if (StringUtils.equals(type, "login") && !userExists(mobile)) throw new MomiaFailedException("用户不存在，请先注册");

        String code = getGeneratedCode(mobile);
        if (StringUtils.isBlank(code)) {
            boolean outOfDate = (code != null); // null 表示没有生成过，空表示生成过，但已过期
            code = generateCode(mobile);
            updateCode(mobile, code, outOfDate);
        }
        Date lastSendTime = getLastSendTime(mobile);
        if (lastSendTime != null && new Date().getTime() - lastSendTime.getTime() < 60 * 1000) return true;

        sendAsync(mobile, buildCodeMsg(code));

        return true;
    }

    private boolean userExists(String mobile) {
        String sql = "SELECT id FROM t_user WHERE mobile=?";

        return jdbcTemplate.query(sql, new Object[] { mobile }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }) > 0;
    }

    private String getGeneratedCode(String mobile)
    {
        String sql = "SELECT code, status, generateTime FROM t_verify WHERE mobile=?";

        return jdbcTemplate.query(sql, new Object[] { mobile }, new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (!rs.next()) return null;

                String code = rs.getString("code");
                int status = rs.getInt("status");
                Date generateTime = rs.getTimestamp("generateTime");

                if (status == 0 || generateTime == null || new Date().getTime() - generateTime.getTime() > 30 * 60 * 1000)
                    return "";
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

    private String buildCodeMsg(String code) {
        return "验证码：" + code + "，30分钟内有效【松果亲子】";
    }

    private void sendAsync(final String mobile, final String codeMsg) {
        if (executorService == null) initExecutorService();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (sender.send(mobile, codeMsg)) {
                    updateSendTime(mobile);
                }
            }
        });
    }

    private synchronized void initExecutorService() {
        if (executorService != null) return;

        int corePoolSize = Configuration.getInt("Sms.CorePoolSize");
        int maxPoolSize = Configuration.getInt("Sms.MaxPoolSize");
        int queueSize = Configuration.getInt("Sms.QueueSize");
        executorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
    }

    private boolean updateSendTime(String mobile)
    {
        String sql = "UPDATE t_verify SET sendTime=NOW() WHERE mobile=?";

        return jdbcTemplate.update(sql, new Object[] { mobile }) == 1;
    }

    @Override
    public boolean verifyCode(String mobile, String code) {
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

    @Override
    public boolean notifyUser(final String mobile, final String msg) {
        if (executorService == null) initExecutorService();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                sender.send(mobile, msg);
            }
        });

        return true;
    }
}
