package cn.momia.service.base.product.sku;

import cn.momia.common.web.misc.SkuUtil;
import cn.momia.common.misc.TimeUtil;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Sku implements Serializable {
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

                if (timeStamp1 <= timeStamp2) return 1;
                return -1;
            }
        });

        return skus;
    }

    private Date startTime() {
        List<Date> times = startEndTimes();
        if (times.isEmpty()) return null;

        Collections.sort(times);
        return times.get(0);
    }

    private long id;
    private long productId;
    private String desc;
    private int type;
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

    public String time() {
        for (SkuProperty property : properties) {
            if ("time".equalsIgnoreCase(property.getName())) return SkuUtil.getSkuTime(property.getValue());
        }

        return "";
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

    public List<Date> startEndTimes() {
        for (SkuProperty property : properties) {
            if ("time".equalsIgnoreCase(property.getName())) {
                return TimeUtil.castToDates(Lists.newArrayList(SkuUtil.TIME_SPLITTER.split(property.getValue())));
            }
        }

        return new ArrayList<Date>();
    }

    public List<SkuPrice> getPrice(int adult, int child) {
        List<SkuPrice> prices = new ArrayList<SkuPrice>();
        for (SkuPrice price : this.prices) {
            if (price.getAdult() == adult && price.getChild() == child) prices.add(price);
        }

        return prices;
    }
}
