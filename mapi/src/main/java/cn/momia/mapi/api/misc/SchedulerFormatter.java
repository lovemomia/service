package cn.momia.mapi.api.misc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SchedulerFormatter {
    private static final DateFormat YEAR_DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("M月d日");
    private static final String[] WEEK_DAYS = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };

    public static String format(List<Date> times) {
        if (times.isEmpty()) return "";
        if (times.size() == 1) {
            Date start = times.get(0);
            return DATE_FORMATTER.format(start) + " " + getWeekDay(start) + " 共1场";
        } else {
            Date start = times.get(0);
            Date end = times.get(times.size() - 1);
            if (isSameDay(start, end)) {
                return DATE_FORMATTER.format(start) + " " + getWeekDay(start) + " 共" + times.size() + "场";
            } else {
                return DATE_FORMATTER.format(start) + "-" + DATE_FORMATTER.format(end) + " " + getWeekDay(start) + "-" + getWeekDay(end) + " 共" + times.size() + "场";
            }
        }
    }

    private static String getWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return WEEK_DAYS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    private static boolean isSameDay(Date start, Date end) {
        return YEAR_DATE_FORMATTER.format(start).equals(YEAR_DATE_FORMATTER.format(end));
    }
}
