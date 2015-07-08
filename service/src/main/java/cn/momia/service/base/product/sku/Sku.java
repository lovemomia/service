package cn.momia.service.base.product.sku;

import cn.momia.common.misc.TimeUtil;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Sku implements Serializable {
    public static final Splitter TIME_SPLITTER = Splitter.on("~").trimResults().omitEmptyStrings();
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("M月d日");
    private static final DateFormat TIME_FORMATTER = new SimpleDateFormat("h:mm");

    public static final Sku NOT_EXIST_SKU = new Sku();
    public static final Sku INVALID_SKU = new Sku();

    static {
        NOT_EXIST_SKU.setId(0);
        INVALID_SKU.setId(0);
    }

    public static List<Sku> sortByTime(List<Sku> skus) {
        Collections.sort(skus, new Comparator<Sku>() {
            @Override
            public int compare(Sku s1, Sku s2) {
                Date time1 = s1.time();
                Date time2 = s2.time();

                if (time1 == null) return -1;
                if (time2 == null) return 1;

                long timeStamp1 = time1.getTime();
                long timeStamp2 = time2.getTime();

                if (timeStamp1 <= timeStamp2) return -1;
                return 1;
            }
        });

        return skus;
    }

    private long id;
    private long productId;
    private List<SkuProperty> properties;
    private List<SkuPrice> prices;
    private int limit;
    private boolean needRealName;
    private int stock;
    private int lockedStock;
    private int unlockedStock;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public List<SkuProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SkuProperty> properties) {
        this.properties = properties;
    }

    public List<SkuPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<SkuPrice> prices) {
        this.prices = prices;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isNeedRealName() {
        return needRealName;
    }

    public void setNeedRealName(boolean needRealName) {
        this.needRealName = needRealName;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getLockedStock() {
        return lockedStock;
    }

    public void setLockedStock(int lockedStock) {
        this.lockedStock = lockedStock;
    }

    public int getUnlockedStock() {
        return unlockedStock;
    }

    public void setUnlockedStock(int unlockedStock) {
        this.unlockedStock = unlockedStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sku)) return false;

        Sku sku = (Sku) o;

        return getId() == sku.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_SKU);
    }

    private Date time() {
        for (SkuProperty property : properties) {
            if ("time".equalsIgnoreCase(property.getName())) {
                List<Date> times = castToDate(Lists.newArrayList(TIME_SPLITTER.split(property.getValue())));
                if (times.isEmpty()) return null;
                Collections.sort(times);
                return times.get(0);
            }
        }

        return null;
    }

    public String formatTime() {
        String timeValue = null;
        for (SkuProperty property : properties) {
            if ("time".equalsIgnoreCase(property.getName())) {
                timeValue = property.getValue();
                break;
            }
        }

        if (StringUtils.isBlank(timeValue)) return "";

        List<String> timeStrs = Lists.newArrayList(TIME_SPLITTER.split(timeValue));
        if (timeStrs.isEmpty()) return "";

        Collections.sort(timeStrs);
        List<Date> times = castToDate(timeStrs);
        if (times.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();
        Date start = times.get(0);
        Date end = times.get(timeStrs.size() - 1);
        if (TimeUtil.isSameDay(start, end)) {
            String timeStr = timeStrs.get(0);
            Date time = castToDate(timeStr);
            if (time != null) {
                builder.append(buildDateWithWeekDay(time));
                if (timeStr.contains(":"))
                    builder.append(TimeUtil.getAmPm(time))
                            .append(TIME_FORMATTER.format(time));
            }
        } else {
            builder.append(buildDateWithWeekDay(start))
                    .append("~")
                    .append(buildDateWithWeekDay(end));
        }

        return builder.toString();
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

    private static String buildDateWithWeekDay(Date time) {
        StringBuilder builder = new StringBuilder();
        builder.append(DATE_FORMATTER.format(time))
                .append("(")
                .append(TimeUtil.getWeekDay(time))
                .append(")");

        return builder.toString();
    }
}
