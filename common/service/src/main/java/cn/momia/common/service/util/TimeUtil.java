package cn.momia.common.service.util;

import com.alibaba.fastjson.util.TypeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeUtil {
    private static final DateFormat YEAR_DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("M月d日");
    private static final String[] WEEK_DAYS = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
    private static final String[] AM_PM = { "上午", "下午" };

    public static Date castToDates(String timeStr) {
        try {
            return TypeUtils.castToDate(timeStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Date> castToDates(List<String> timeStrs) {
        List<Date> times = new ArrayList<Date>();
        for (String timeStr : timeStrs) {
            Date time = castToDates(timeStr);
            if (time != null) times.add(time);
        }

        return times;
    }

    public static String buildDateWithWeekDay(Date time) {
        StringBuilder builder = new StringBuilder();
        builder.append(DATE_FORMATTER.format(time))
                .append("(")
                .append(TimeUtil.getWeekDay(time))
                .append(")");

        return builder.toString();
    }

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

    public static String buildMonthStr(int month) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        if (month < currentMonth) return String.format("%d-%02d", currentYear + 1, month);
        return String.format("%d-%02d", currentYear, month);
    }

    public static String buildNextMonthStr(int month) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        int nextMonth = month + 1;
        nextMonth = nextMonth > 12 ? nextMonth - 12 : nextMonth;

        if (month < currentMonth || nextMonth < month) return String.format("%d-%02d", currentYear + 1, nextMonth);
        return String.format("%d-%02d", currentYear, nextMonth);
    }
}
