package cn.momia.mapi.api.misc;

import cn.momia.mapi.api.v1.dto.base.ProductDto;
import cn.momia.mapi.img.ImageFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProductUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductUtil.class);

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("M月d日");
    private static final DateFormat TIME_FORMATTER = new SimpleDateFormat("h:mm");

    public static List<ProductDto> extractProductsData(JSONArray productsJson) {
        List<ProductDto> products = new ArrayList<ProductDto>();

        for (int i = 0; i < productsJson.size(); i++) {
            try {
                ProductDto product = new ProductDto();

                JSONObject productJson = productsJson.getJSONObject(i);
                JSONObject placeJson = productJson.getJSONObject("place");
                JSONArray skusJson = productJson.getJSONArray("skus");

                product.setId(productJson.getLong("id"));
                product.setCover(ImageFile.url(productJson.getString("cover")));
                product.setTitle(productJson.getString("title"));
                product.setAddress(placeJson.getString("address"));
                product.setPoi(StringUtils.join(new Object[] { placeJson.getDouble("lng"), placeJson.getDouble("lat") }, ":"));
                product.setScheduler(ProductUtil.getScheduler(skusJson));
                product.setJoined(productJson.getInteger("sales"));
                product.setPrice(ProductUtil.getMiniPrice(skusJson));

                products.add(product);
            } catch (Exception e) {
                LOGGER.error("fail to parse product: ", productsJson.getJSONObject(i), e);
            }
        }

        return products;
    }

    public static BigDecimal getMiniPrice(JSONArray skusJson) {
        BigDecimal miniPrice = new BigDecimal(Float.MAX_VALUE);

        for (int i = 0; i < skusJson.size(); i++) {
            JSONObject skuJson = skusJson.getJSONObject(i);
            JSONArray pricesJson = skuJson.getJSONArray("prices");
            BigDecimal price = getSkuMiniPrice(pricesJson);
            if (price.compareTo(miniPrice) <= 0) miniPrice = price;
        }

        return miniPrice;
    }

    public static BigDecimal getSkuMiniPrice(JSONArray pricesJson) {
        BigDecimal miniPrice = new BigDecimal(Float.MAX_VALUE);

        for (int i = 0; i < pricesJson.size(); i++) {
            JSONObject priceJson = pricesJson.getJSONObject(i);
            BigDecimal price = priceJson.getBigDecimal("price");
            if (price.compareTo(miniPrice) <= 0) miniPrice = price;
        }

        return miniPrice;
    }

    public static String getScheduler(JSONArray skusJson) {
        List<Date> times = new ArrayList<Date>();
        for (int i = 0; i < skusJson.size(); i++) {
            JSONObject skuJson = skusJson.getJSONObject(i);
            JSONArray propertiesJson = skuJson.getJSONArray("properties");
            List<String> timeStrs = extractSkuTimes(propertiesJson);
            times.addAll(castToDate(timeStrs));
        }

        Collections.sort(times);

        return ProductUtil.format(times);
    }

    private static List<Date> castToDate(List<String> timeStrs) {
        List<Date> times = new ArrayList<Date>();
        for (String timeStr : timeStrs) {
            Date time = castToDate(timeStr);
            if (time != null) times.add(time);
        }

        return times;
    }

    private static Date castToDate(String timeStr) {
        try {
            return TypeUtils.castToDate(timeStr);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<String> extractSkuTimes(JSONArray propertiesJson) {
        List<String> timeStrs = new ArrayList<String>();

        for (int i = 0; i < propertiesJson.size(); i++) {
            JSONObject propertyJson = propertiesJson.getJSONObject(i);
            if (propertyJson.getString("name").equals("time")) {
                for (String timeStr : Splitter.on("~").trimResults().omitEmptyStrings().split(propertyJson.getString("value"))) {
                    timeStrs.add(timeStr);
                }
            }
        }

        return timeStrs;
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

    public static String getSkuScheduler(JSONArray propertiesJson) {
        List<String> timeStrs = extractSkuTimes(propertiesJson);
        StringBuilder builder = new StringBuilder();
        if (timeStrs.size() == 1) {
            String timeStr = timeStrs.get(0);
            Date time = castToDate(timeStr);
            if (time != null) {
                builder.append(buildDateWithWeekDay(time));
                if (timeStr.contains(":"))
                    builder.append(TimeUtil.getAmPm(time))
                            .append(TIME_FORMATTER.format(time));
            }
        } else if (timeStrs.size() > 1) {
            List<Date> times = castToDate(timeStrs);
            if (times.size() > 1) {
                Collections.sort(times);
                Date start = times.get(0);
                Date end = times.get(timeStrs.size() - 1);
                builder.append(buildDateWithWeekDay(start))
                        .append("~")
                        .append(buildDateWithWeekDay(end));
            } else {
                Date time = times.get(0);
                builder.append(buildDateWithWeekDay(time));
            }
        }

        return builder.toString();
    }

    private static String buildDateWithWeekDay(Date time) {
        StringBuilder builder = new StringBuilder();
        builder.append(DATE_FORMATTER.format(time))
                .append("(")
                .append(TimeUtil.getWeekDay(time))
                .append(")");

        return builder.toString();
    }
}
