package cn.momia.service.course.activity.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.api.course.activity.Activity;
import cn.momia.api.course.activity.ActivityEntry;
import cn.momia.service.course.activity.ActivityService;
import cn.momia.service.course.activity.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ActivityServiceImpl extends AbstractService implements ActivityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Override
    public Activity getActivity(int activityId) {
        String sql = "SELECT Id, Cover, Title, `Desc`, Message, NeedPay, Price, StartTime, EndTime FROM SG_Activity WHERE Id=? AND OnlineTime<=NOW() AND OfflineTime>NOW() AND Status=1";
        return queryObject(sql, new Object[] { activityId }, Activity.class, Activity.NOT_EXIST_ACTIVITY);
    }

    @Override
    public ActivityEntry getActivityEntry(long entryId) {
        String sql = "SELECT Id, ActivityId, Mobile, ChildName, Status FROM SG_ActivityEntry WHERE Id=?";
        return queryObject(sql, new Object[] { entryId }, ActivityEntry.class, ActivityEntry.NOT_EXIST_ACTIVITY_ENTRY);
    }

    @Override
    public boolean joined(int activityId, String mobile, String childName) {
        String sql = "SELECT COUNT(1) FROM SG_ActivityEntry WHERE ActivityId=? AND Mobile=? AND ChildName=? AND Status=?";
        return queryInt(sql, new Object[] { activityId, mobile, childName, ActivityEntry.Status.PAYED }) > 0;
    }

    @Override
    public long join(final int activityId, final String mobile, final String childName, final String relation, final int status) {
        return insert(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_ActivityEntry (ActivityId, Mobile, ChildName, RelationShip, Status, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, activityId);
                ps.setString(2, mobile);
                ps.setString(3, childName);
                ps.setString(4, relation);
                ps.setInt(5, status);

                return ps;
            }
        }).getKey().longValue();
    }

    @Override
    public boolean prepay(long entryId) {
        String sql = "UPDATE SG_ActivityEntry SET Status=? WHERE Id=? AND (Status=? OR Status=?)";
        int updateCount = singleUpdate(sql, new Object[] { ActivityEntry.Status.PRE_PAYED, entryId, ActivityEntry.Status.NOT_PAYED, ActivityEntry.Status.PRE_PAYED });

        return updateCount == 1;
    }

    @Override
    public boolean pay(final Payment payment) {
        try {
            execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    payActivity(payment.getOrderId());
                    logPayment(payment);

                    return null;
                }
            });
        } catch (Exception e) {
            LOGGER.error("fail to pay activity entry: {}", payment.getOrderId(), e);
            return false;
        }

        return true;
    }

    private void payActivity(long entryId) {
        String sql = "UPDATE SG_ActivityEntry SET Status=? WHERE Id=? AND Status=?";
        int updateCount = singleUpdate(sql, new Object[] { ActivityEntry.Status.PAYED, entryId, ActivityEntry.Status.PRE_PAYED });

        if (updateCount != 1) throw new RuntimeException("fail to pay activity, entry id: " + entryId);
    }

    private void logPayment(final Payment payment) {
        String sql = "INSERT INTO SG_ActivityPayment(OrderId, Payer, FinishTime, PayType, TradeNo, Fee, AddTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
        int updateCount = singleUpdate(sql, new Object[] { payment.getOrderId(), payment.getPayer(), payment.getFinishTime(), payment.getPayType(), payment.getTradeNo(), payment.getFee() });

        if (updateCount != 1) throw new RuntimeException("fail to log payment for activity entry: " + payment.getOrderId());
    }
}
