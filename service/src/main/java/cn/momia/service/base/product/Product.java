package cn.momia.service.base.product;

import cn.momia.common.misc.TimeUtil;
import cn.momia.service.base.product.base.BaseProduct;
import cn.momia.service.base.product.place.Place;
import cn.momia.service.base.product.sku.Sku;
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
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("M月d日");

    public static final Product NOT_EXIST_PRODUCT = new Product();

    private BaseProduct baseProduct;
    private List<ProductImage> imgs;
    private Place place;
    private List<Sku> skus;

    public long getId() {
        if (baseProduct == null || !baseProduct.exists()) return 0;
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

    public Date getStartTime() {
        return baseProduct.getStartTime();
    }

    public Date getEndTime() {
        return baseProduct.getEndTime();
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

    public String getScheduler() {
        List<Date> times = new ArrayList<Date>();
        for (Sku sku : skus) {
            times.addAll(sku.startEndTimes());
        }
        Collections.sort(times);

        return format(times, skus.size());
    }

    private String format(List<Date> times, int count) {
        if (times.isEmpty()) return "";
        if (times.size() == 1) {
            Date start = times.get(0);
            return DATE_FORMATTER.format(start) + " " + TimeUtil.getWeekDay(start) + " 共" + count + "场";
        } else {
            Date start = times.get(0);
            Date end = times.get(times.size() - 1);
            if (TimeUtil.isSameDay(start, end)) {
                return DATE_FORMATTER.format(start) + " " + TimeUtil.getWeekDay(start) + " 共" + count + "场";
            } else {
                return DATE_FORMATTER.format(start) + "-" + DATE_FORMATTER.format(end) + " " + TimeUtil.getWeekDay(start) + "-" + TimeUtil.getWeekDay(end) + " 共" + count + "场";
            }
        }
    }

    public BigDecimal getMinPrice() {
        if(skus == null || skus.isEmpty()) return new BigDecimal(0);

        BigDecimal miniPrice = new BigDecimal(Float.MAX_VALUE);
        for (Sku sku : skus) {
            BigDecimal price = sku.getMinPrice();
            if (price.compareTo(miniPrice) <= 0) miniPrice = price;
        }

        return miniPrice;
    }

    public boolean invalid() {
        return !(baseProduct != null && baseProduct.exists() &&
                imgs != null && !imgs.isEmpty() &&
                place != null && place.exists() &&
                skus != null && !skus.isEmpty());
    }
}
