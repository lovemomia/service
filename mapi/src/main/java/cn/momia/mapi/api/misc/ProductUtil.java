package cn.momia.mapi.api.misc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductUtil {
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("M月d日");
    private static final DateFormat TIME_FORMATTER = new SimpleDateFormat("h:mm");

    public static float getMiniPrice(JSONArray skus) {
        float miniPrice = Float.MAX_VALUE;

        for (int i = 0; i < skus.size(); i++) {
            JSONObject sku = skus.getJSONObject(i);
            JSONArray pricesOfSku = sku.getJSONArray("prices");
            float price = getSkuMiniPrice(pricesOfSku);
            if (price < miniPrice) miniPrice = price;
        }

        return miniPrice;
    }

    public static float getSkuMiniPrice(JSONArray prices) {
        float miniPrice = Float.MAX_VALUE;

        for (int i = 0; i < prices.size(); i++) {
            JSONObject priceObject = prices.getJSONObject(i);
            float price = priceObject.getFloat("price");
            if (price < miniPrice) miniPrice = price;
        }

        return miniPrice;
    }

    public static String getScheduler(JSONArray skus) {
        List<Date> times = new ArrayList<Date>();
        for (int i = 0; i < skus.size(); i++) {
            JSONObject sku = skus.getJSONObject(i);
            JSONArray properties = sku.getJSONArray("properties");
            Date time = parseSkuTime(properties);
            if (time != null) times.add(time);
        }

        return ProductUtil.format(times);
    }

    private static Date parseSkuTime(JSONArray properties) {
        for (int i = 0; i < properties.size(); i++) {
            JSONObject property = properties.getJSONObject(i);
            if (property.getString("name").equals("时间")) {
                return property.getDate("value");
            }
        }

        return null;
    }

    private static String format(List<Date> times) {
        if (times.isEmpty()) return "";
        if (times.size() == 1) {
            Date start = times.get(0);
            return DATE_FORMATTER.format(start) + " " + TimeUtil.getWeekDay(start) + " 共1场";
        } else {
            Date start = times.get(0);
            Date end = times.get(times.size() - 1);
            if (TimeUtil.isSameDay(start, end)) {
                return DATE_FORMATTER.format(start) + " " + TimeUtil.getWeekDay(start) + " 共" + times.size() + "场";
            } else {
                return DATE_FORMATTER.format(start) + "-" + DATE_FORMATTER.format(end) + " " + TimeUtil.getWeekDay(start) + "-" + TimeUtil.getWeekDay(end) + " 共" + times.size() + "场";
            }
        }
    }

    public static String getSkuTime(JSONArray properties) {
        Date time = parseSkuTime(properties);
        StringBuilder builder = new StringBuilder();
        if (time != null) {
            builder.append(DATE_FORMATTER.format(time))
                    .append("(")
                    .append(TimeUtil.getWeekDay(time))
                    .append(")")
                    .append(TimeUtil.getAmPm(time))
                    .append(TIME_FORMATTER.format(time));
        }

        return builder.toString();
    }
}
