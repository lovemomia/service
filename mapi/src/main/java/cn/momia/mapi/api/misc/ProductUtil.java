package cn.momia.mapi.api.misc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProductUtil {
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("M月d日");

    public static float getPrice(JSONArray skus) {
        List<Float> prices = new ArrayList<Float>();
        for (int i = 0; i < skus.size(); i++) {
            JSONObject sku = skus.getJSONObject(i);
            JSONArray pricesOfSku = sku.getJSONArray("prices");
            for (int j = 0; j < pricesOfSku.size(); j++) {
                JSONObject priceObject = pricesOfSku.getJSONObject(j);
                prices.add(priceObject.getFloat("price"));
            }
        }
        Collections.sort(prices);

        return prices.isEmpty() ? 0 : prices.get(0);
    }

    public static String getScheduler(JSONArray skus) {
        List<Date> times = new ArrayList<Date>();
        for (int i = 0; i < skus.size(); i++) {
            JSONObject sku = skus.getJSONObject(i);
            JSONArray properties = sku.getJSONArray("properties");
            Date time = getTime(properties);
            if (time != null) times.add(time);
        }

        return ProductUtil.format(times);
    }

    public static Date getTime(JSONArray properties) {
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
}
