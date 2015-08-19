package cn.momia.service.product.sku;

import cn.momia.service.base.util.TimeUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Sku implements Serializable {
    private static final Splitter TIME_SPLITTER = Splitter.on("~").trimResults().omitEmptyStrings();
    private static final DateFormat TIME_FORMATTER = new SimpleDateFormat("h:mm");

    public static class Type {
        public static final int NORMAL = 0;
        public static final int NO_CEILING = 1;
    }

    public static final Sku NOT_EXIST_SKU = new Sku();
    public static final Sku INVALID_SKU = new Sku();

    static {
        NOT_EXIST_SKU.setId(0);
        INVALID_SKU.setId(0);
    }

    public static List<Sku> sort(List<Sku> skus) {
        Collections.sort(skus, new Comparator<Sku>() {
            @Override
            public int compare(Sku s1, Sku s2) {
                if (s1.isFull() && s2.isFull()) return 0;
                if (s1.isFull()) return 1;
                if (s2.isFull()) return -1;

                Date time1 = s1.startTime();
                Date time2 = s2.startTime();

                if (time1 == null) return 1;
                if (time2 == null) return -1;

                long timeStamp1 = time1.getTime();
                long timeStamp2 = time2.getTime();

                if (timeStamp1 <= timeStamp2) return -1;
                return 1;
            }
        });

        return skus;
    }

    public static List<Sku> sortByStartTime(List<Sku> skus) {
        Collections.sort(skus, new Comparator<Sku>() {
            @Override
            public int compare(Sku s1, Sku s2) {
                Date time1 = s1.startTime();
                Date time2 = s2.startTime();

                if (time1 == null) return 1;
                if (time2 == null) return -1;

                long timeStamp1 = time1.getTime();
                long timeStamp2 = time2.getTime();

                if (timeStamp1 <= timeStamp2) return -1;
                return 1;
            }
        });

        return skus;
    }

    private Date startTime() {
        List<Date> times = getStartEndTimes();
        if (times.isEmpty()) return null;

        Collections.sort(times);
        return times.get(0);
    }

    public static List<Sku> filterFinished(List<Sku> skus) {
        List<Sku> filteredSkus = new ArrayList<Sku>();

        Date now = new Date();
        for (Sku sku : skus) {
            if (sku.isFinished(now)) continue;
            filteredSkus.add(sku);
        }

        return filteredSkus;
    }

    public static List<Sku> filterClosed(List<Sku> skus) {
        List<Sku> filteredSkus = new ArrayList<Sku>();

        Date now = new Date();
        for (Sku sku : skus) {
            if (sku.isClosed(now)) continue;
            filteredSkus.add(sku);
        }

        return filteredSkus;
    }

    private long id;
    private long productId;
    private String desc;
    private int type;
    private boolean anyTime;
    private Date startTime;
    private Date endTime;
    private List<SkuProperty> properties;
    private List<SkuPrice> prices;
    private int limit;
    private boolean needRealName;
    private int stock;
    private int lockedStock;
    private int unlockedStock;
    private Date onlineTime;
    private Date offlineTime;
    private boolean onWeekend;
    private boolean needLeader;
    private long leaderUserId;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isAnyTime() {
        return anyTime;
    }

    public void setAnyTime(boolean anyTime) {
        this.anyTime = anyTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Date getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(Date offlineTime) {
        this.offlineTime = offlineTime;
    }

    public boolean isOnWeekend() {
        return onWeekend;
    }

    public void setOnWeekend(boolean onWeekend) {
        this.onWeekend = onWeekend;
    }

    public boolean isNeedLeader() {
        return needLeader;
    }

    public void setNeedLeader(boolean needLeader) {
        this.needLeader = needLeader;
    }

    public long getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(long leaderUserId) {
        this.leaderUserId = leaderUserId;
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

    public boolean hasLeader() {
        return leaderUserId > 0;
    }

    public boolean isNoCeiling() {
        return type == Type.NO_CEILING;
    }

    public String getFormatedTime() {
        for (SkuProperty property : properties) {
            if ("time".equalsIgnoreCase(property.getName())) return formatTime(property.getValue());
        }

        return "";
    }

    private String formatTime(String timeValue) {
        if (StringUtils.isBlank(timeValue)) return "";

        List<String> timeStrs = Lists.newArrayList(TIME_SPLITTER.split(timeValue));
        if (timeStrs.isEmpty()) return "";

        Collections.sort(timeStrs);
        List<Date> times = TimeUtil.castToDates(timeStrs);
        if (times.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();

        Date start = times.get(0);
        Date end = times.get(timeStrs.size() - 1);
        if (TimeUtil.isSameDay(start, end)) {
            for (String timeStr : timeStrs) {
                Date time = TimeUtil.castToDate(timeStr);
                if (time != null) {
                    builder.append(TimeUtil.formatDateWithWeekDay(time));
                    if (timeStr.contains(":"))
                        builder.append(TimeUtil.getAmPm(time))
                                .append(TIME_FORMATTER.format(time));
                    break;
                }
            }
        } else {
            builder.append(TimeUtil.formatDateWithWeekDay(start))
                    .append("~")
                    .append(TimeUtil.formatDateWithWeekDay(end));
        }

        return builder.toString();
    }

    public BigDecimal getMinPrice() {
        if (prices == null || prices.isEmpty()) return new BigDecimal(0);

        BigDecimal minPrice = new BigDecimal(Float.MAX_VALUE);
        for (SkuPrice skuPrice : prices) {
            BigDecimal price = skuPrice.getPrice();
            if (price.compareTo(minPrice) <= 0) minPrice = price;
        }

        return minPrice;
    }

    public BigDecimal getMinOriginalPrice() {
        if (prices == null || prices.isEmpty()) return new BigDecimal(0);

        SkuPrice minSkuPrice = null;
        BigDecimal minPrice = new BigDecimal(Float.MAX_VALUE);
        for (SkuPrice skuPrice : prices) {
            BigDecimal price = skuPrice.getPrice();
            if (price.compareTo(minPrice) <= 0) {
                minSkuPrice = skuPrice;
                minPrice = price;
            }
        }

        return minSkuPrice == null ? new BigDecimal(0) : minSkuPrice.getOrigin();
    }

    public List<Date> getStartEndTimes() {
        for (SkuProperty property : properties) {
            if ("time".equalsIgnoreCase(property.getName())) {
                return TimeUtil.castToDates(Lists.newArrayList(TIME_SPLITTER.split(property.getValue())));
            }
        }

        return new ArrayList<Date>();
    }

    public boolean isFinished(Date now) {
        if (offlineTime.before(now) || (startTime.before(now) && !anyTime)) return true;

        return false;
    }

    public boolean isFull() {
        return type != 1 && unlockedStock <= 0;
    }

    public boolean isClosed(Date now) {
        if (isFinished(now) || isFull()) return true;

        return false;
    }

    public boolean findPrice(int adult, int child, BigDecimal price) {
        for (SkuPrice skuPrice : this.prices) {
            if (skuPrice.getAdult() == adult &&
                    skuPrice.getChild() == child &&
                    skuPrice.getPrice().compareTo(price) == 0) return true;
        }

        return false;
    }
}
