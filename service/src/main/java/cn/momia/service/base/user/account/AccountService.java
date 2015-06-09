package cn.momia.service.base.user.account;

import java.util.Date;
import java.util.List;

public interface AccountService {
    class TimeUnit {
        public static final int YEAR = 0;
        public static final int MONTH = 1;
        public static final int WEEK = 2;
        public static final int DAY = 3;
    }

    Account get(long userId);
    float getTodayIncome(long userId);
    float getTotalIncome(long userId);
    List<AccountStatistic> getIncomeStatistic(long userId, Date startTime, Date endTime, int timeUnit);
    boolean withDraw(long userId, float amount);
}
