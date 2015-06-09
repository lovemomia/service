package cn.momia.service.base.user.account.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.user.account.Account;
import cn.momia.service.base.user.account.AccountService;
import cn.momia.service.base.user.account.AccountStatistic;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AccountServiceImpl extends DbAccessService implements AccountService {
    @Override
    public Account get(long userId) {
        Account account = getAccount(userId);
        if (account.exists()) {
            account.setToConfirm(getToConfirm(userId));
            account.setToRefund(getToRefund(userId));
            account.setAward(getAward(userId));
        }

        return account;
    }

    private Account getAccount(long userId) {
        String sql = "SELECT id, userId, balance FROM t_user_account WHERE userId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { userId }, new ResultSetExtractor<Account>() {
            @Override
            public Account extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (!rs.next()) return Account.NOT_EXIST_ACCOUNT;

                Account account = new Account();
                account.setId(rs.getLong("id"));
                account.setUserId(rs.getLong("userId"));
                account.setBalance(rs.getFloat("balance"));

                return account;
            }
        });
    }

    private float getToConfirm(long userId) {
        String sql = "SELECT SUM(income) FROM t_clearing WHERE userId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { userId }, new ResultSetExtractor<Float>() {
            @Override
            public Float extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getFloat(1);
                return 0F;
            }
        });
    }

    private float getToRefund(long userId) {
        String sql = "SELECT SUM(amount) FROM t_refund WHERE userId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { userId }, new ResultSetExtractor<Float>() {
            @Override
            public Float extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getFloat(1);
                return 0F;
            }
        });
    }

    private float getAward(long userId) {
        String sql = "SELECT SUM(amount) FROM t_award WHERE userId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { userId }, new ResultSetExtractor<Float>() {
            @Override
            public Float extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getFloat(1);
                return 0F;
            }
        });
    }

    @Override
    public float getTotalIncome(long userId) {
        return 0;
    }

    @Override
    public float getTodayIncome(long userId) {
        return 0;
    }

    @Override
    public List<AccountStatistic> getIncomeStatistic(long userId, Date startTime, Date endTime, int timeUnit) {
        return null;
    }

    @Override
    public boolean withDraw(long userId, float amount) {
        return false;
    }
}
