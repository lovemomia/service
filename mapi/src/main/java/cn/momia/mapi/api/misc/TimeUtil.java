package cn.momia.mapi.api.misc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    private static final DateFormat YEAR_DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
    private static final String[] WEEK_DAYS = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
    private static final String[] AM_PM = { "上午", "下午" };

    public static String getWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return WEEK_DAYS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public static boolean isSameDay(Date day1, Date day2) {
        return YEAR_DATE_FORMATTER.format(day1).equals(YEAR_DATE_FORMATTER.format(day2));
    }

    public static String getAmPm(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return AM_PM[calendar.get(Calendar.AM_PM)];
    }
}
