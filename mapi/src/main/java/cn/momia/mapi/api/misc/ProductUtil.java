package cn.momia.mapi.api.misc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProductUtil {
    private static final DateFormat YEAR_DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("M月d日");
    private static final String[] WEEK_DAYS = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };

    public static float getPrice(JSONArray skus) {
        List<Float> prices = new ArrayList<Float>();
        for (int i = 0; i < skus.size(); i++) {
            JSONObject sku = skus.getJSONObject(i);
            prices.add(sku.getFloat("price"));
        }
        Collections.sort(prices);

        return prices.isEmpty() ? 0 : prices.get(0);
    }

    public static String getScheduler(JSONArray skus) {
        List<Date> times = new ArrayList<Date>();
        for (int i = 0; i < skus.size(); i++) {
            JSONObject sku = skus.getJSONObject(i);
            JSONArray proterties = sku.getJSONArray("properties");
            for (int j = 0; j < proterties.size(); j++) {
                JSONObject property = proterties.getJSONObject(j);
                if (property.getString("name").equals("时间")) {
                    times.add(property.getDate("value"));
                }
            }
        }

        return ProductUtil.format(times);
    }

    private static String format(List<Date> times) {
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
