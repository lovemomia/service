package cn.momia.service.base.sms.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.AbstractService;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.service.base.sms.SmsSender;
import cn.momia.service.base.sms.SmsService;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SmsServiceImpl extends AbstractService implements SmsService {
    private ExecutorService executorService;

    private SmsSender sender;

    public void setSender(SmsSender sender) {
        this.sender = sender;
    }

    @Override
    public boolean sendCode(String mobile) {
        checkFrequency(mobile);

        String code = getOrGenerateCode(mobile);
        sendAsync(mobile, buildCodeMsg(code));

        return true;
    }

    private void checkFrequency(String mobile) {
        Date lastSendTime = getLastSendTime(mobile);
        if (lastSendTime != null && new Date().getTime() - lastSendTime.getTime() < 60 * 1000)
            throw new MomiaFailedException("发送频率过快，请稍后再试");
    }

    private Date getLastSendTime(String mobile) {
        String sql = "SELECT SendTime FROM SG_Verify WHERE Mobile=?";
        return queryDate(sql, new Object[] { mobile }, null);
    }

    private String getOrGenerateCode(String mobile) {
        String code = getGeneratedCode(mobile);
        if (StringUtils.isBlank(code)) {
            code = generateCode(mobile);
            updateCode(mobile, code);
        }

        return code;
    }

    private String getGeneratedCode(String mobile) {
        String sql = "SELECT Code FROM SG_Verify WHERE Mobile=? AND GenerateTime>? AND Status=1";
        return queryString(sql, new Object[] { mobile, new Date(new Date().getTime() - 30 * 60 * 1000) }, null);
    }

    private String generateCode(String mobile) {
        int number = (int) (Math.random() * 1000000);
        return String.format("%06d", number);
    }

    private void updateCode(String mobile, String code) {
        if (exists(mobile)) {
            String sql = "UPDATE SG_Verify SET Mobile=?, Code=?, GenerateTime=NOW(), SendTime=NULL, Status=1 WHERE Mobile=?";
            update(sql, new Object[] { mobile, code, mobile });
        } else {
            String sql = "INSERT INTO SG_Verify(Mobile, Code, GenerateTime) VALUES (?, ?, NOW())";
            update(sql, new Object[] { mobile, code });
        }
    }

    private boolean exists(String mobile) {
        String sql = "SELECT COUNT(1) FROM SG_Verify WHERE Mobile=?";
        return queryInt(sql, new Object[] { mobile }) > 0;
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

    private boolean updateSendTime(String mobile) {
        String sql = "UPDATE SG_Verify SET SendTime=NOW() WHERE Mobile=?";
        return update(sql, new Object[] { mobile });
    }

    @Override
    public boolean verifyCode(String mobile, String code) {
        String sql = "SELECT COUNT(1) FROM SG_Verify WHERE Mobile=? AND Code=? AND GenerateTime>? AND Status=1";
        boolean successful = queryInt(sql, new Object[] { mobile, code, new Date(new Date().getTime() - 30 * 60 * 1000) }) > 0;

        if (successful) disable(mobile, code);

        return successful;
    }

    private void disable(String mobile, String code) {
        String sql = "UPDATE SG_Verify SET Status=0 WHERE Mobile=? AND Code=?";
        update(sql, new Object[] { mobile, code });
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
