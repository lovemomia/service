package cn.momia.service.product.facade;

import cn.momia.service.base.util.TimeUtil;
import cn.momia.service.product.base.BaseProduct;
import cn.momia.service.product.place.Place;
import cn.momia.service.product.sku.Sku;
import com.alibaba.fastjson.JSONArray;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Product implements Serializable {
    public static class Type {
        public static final int MINI = 1;
        public static final int BASE = 2;
        public static final int BASE_WITH_SKU = 3;
        public static final int FULL = 4;
    }

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("M月d日");

    public static final Product NOT_EXIST_PRODUCT = new Product() {
        @Override
        public long getId() {
            return 0;
        }
    };

    private BaseProduct baseProduct;
    private List<ProductImage> imgs;
    private Place place;
    private List<Sku> skus;

    public long getId() {
        return baseProduct.getId();
    }

    public int getCityId() {
        return baseProduct.getCityId();
    }

    public List<String> getTags() {
        return baseProduct.getTags();
    }

    public String getTitle() {
        return baseProduct.getTitle();
    }

    public String getAbstracts(){ return baseProduct.getAbstracts(); }

    public String getCover() {
        return baseProduct.getCover();
    }

    public String getThumb() {
        return baseProduct.getThumb();
    }

    public String getCrowd() {
        return baseProduct.getCrowd();
    }

    public JSONArray getContent() {
        return baseProduct.getContent();
    }

    public int getJoined() {
        return baseProduct.getJoined();
    }

    public int getSales() {
        return baseProduct.getSales();
    }

    public boolean isSoldOut() {
        return baseProduct.isSoldOut();
    }

    public Date getOnlineTime() {
        return baseProduct.getOnlineTime();
    }

    public Date getOfflineTime() {
        return baseProduct.getOfflineTime();
    }

    public int getStatus() {
        return baseProduct.getStatus();
    }

    public boolean isOpened() {
        Date now = new Date();
        if (getOfflineTime().before(now) || isSoldOut() || getStatus() != 1) return false;

        return !Sku.filter(skus).isEmpty();
    }

    public void setBaseProduct(BaseProduct baseProduct) {
        this.baseProduct = baseProduct;
    }

    public List<ProductImage> getImgs() {
        return imgs;
    }

    public void setImgs(List<ProductImage> imgs) {
        this.imgs = imgs;
    }

    public int getRegionId() {
        return this.place.getRegionId();
    }

    public String getAddress() {
        return this.place.getAddress();
    }

    public String getPoi() {
        return this.place.getLng() + ":" + this.place.getLat();
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        return getId() == product.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_PRODUCT);
    }

    public boolean isInvalid() {
        return !(baseProduct != null && baseProduct.exists() &&
                imgs != null && !imgs.isEmpty() &&
                place != null && place.exists() &&
                skus != null && !skus.isEmpty());
    }

    public String getScheduler() {
        if(skus == null || skus.isEmpty()) return "";

        List<Date> times = new ArrayList<Date>();
        for (Sku sku : skus) {
            times.addAll(sku.getStartEndTimes());
        }
        Collections.sort(times);

        return format(times);
    }

    private String format(List<Date> times) {
        if (times.isEmpty()) return "";
        if (times.size() == 1) {
            Date start = times.get(0);
            return DATE_FORMATTER.format(start) + " " + TimeUtil.getWeekDay(start);
        } else {
            Date start = times.get(0);
            Date end = times.get(times.size() - 1);
            if (TimeUtil.isSameDay(start, end)) {
                return DATE_FORMATTER.format(start) + " " + TimeUtil.getWeekDay(start);
            } else {
                return DATE_FORMATTER.format(start) + "-" + DATE_FORMATTER.format(end);
            }
        }
    }

    public String getWeekendScheduler() {
        if(skus == null || skus.isEmpty()) return "";

        List<Date> times = new ArrayList<Date>();
        for (Sku sku : skus) {
            if (sku.isOnWeekend()) times.addAll(sku.getStartEndTimes());
        }
        Collections.sort(times);

        return formatWeekend(times);
    }

    private String formatWeekend(List<Date> times) {
        if (times.isEmpty()) return "";
        if (times.size() == 1) {
            Date start = times.get(0);
            return DATE_FORMATTER.format(start) + " " + TimeUtil.getWeekDay(start);
        } else {
            Date start = times.get(0);
            Date end = times.get(times.size() - 1);
            if (TimeUtil.isSameDay(start, end)) {
                return DATE_FORMATTER.format(start) + " " + TimeUtil.getWeekDay(start);
            } else {
                return DATE_FORMATTER.format(start) + "-" + DATE_FORMATTER.format(end) + " 多个周末";
            }
        }
    }

    public BigDecimal getMinPrice() {
        if (skus == null || skus.isEmpty()) return new BigDecimal(0);

        BigDecimal miniPrice = new BigDecimal(Float.MAX_VALUE);
        for (Sku sku : skus) {
            BigDecimal price = sku.getMinPrice();
            if (price.compareTo(miniPrice) <= 0) miniPrice = price;
        }

        return miniPrice;
    }

    public BigDecimal getMinOriginalPrice() {
        if (skus == null || skus.isEmpty()) return new BigDecimal(0);

        Sku minPriceSku = null;
        BigDecimal miniPrice = new BigDecimal(Float.MAX_VALUE);
        for (Sku sku : skus) {
            BigDecimal price = sku.getMinPrice();
            if (price.compareTo(miniPrice) <= 0) {
                minPriceSku = sku;
                miniPrice = price;
            }
        }

        return minPriceSku == null ? new BigDecimal(0) : minPriceSku.getMinOriginalPrice();
    }

    public int getStock() {
        if (skus == null || skus.isEmpty()) return 0;

        int stock = 0;
        for (Sku sku : skus) {
            if (sku.isNoCeiling()) return -1;
            stock += sku.getUnlockedStock();
        }

        return stock;
    }

    public String getSkuTime(long skuId) {
        for (Sku sku : skus) {
            if (sku.getId() == skuId) return sku.getFormatedTime();
        }

        return "";
    }

    public boolean isFinished() {
        Date now = new Date();
        for (Sku sku : skus) {
            if (sku.getStartTime().after(now)) return false;
        }

        return true;
    }

    public boolean isEnd() {
        Date now = new Date();
        for (Sku sku : skus) {
            if (sku.getOfflineTime().after(now)) return false;
        }

        return true;
    }
}