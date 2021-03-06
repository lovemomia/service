package cn.momia.api.course.dto.subject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.List;

public class Subject {
    public static class Type {
        public static final int NORMAL = 1;
        public static final int TRIAL = 2;
    }

    public static class Status {
        public static final int OK = 1;
        public static final int SOLD_OUT = 2;
    }

    public static final Subject NOT_EXIST_SUBJECT = new Subject();

    private long id;
    private int type;
    @JSONField(serialize = false) private int cityId;
    private String title;
    private String subTitle;
    private String cover;
    private String tags;
    private String intro;
    private String notice;
    @JSONField(serialize = false) private int stock;
    private List<String> imgs;
    @JSONField(serialize = false) private List<SubjectSku> skus;

    private int status;

    private BigDecimal price;
    private BigDecimal originalPrice;
    private String age = "";
    private int joined;
    private String scheduler;
    private String region;

    private SubjectSku cheapestSku;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public JSONArray getNotice() {
        return JSON.parseArray(notice);
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public List<SubjectSku> getSkus() {
        return skus;
    }

    public void setSkus(List<SubjectSku> skus) {
        this.skus = skus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public SubjectSku getCheapestSku() {
        if (cheapestSku != null) return cheapestSku;

        if (skus == null || skus.isEmpty()) return null;

        SubjectSku cheapestSku = null;
        for (SubjectSku sku : skus) {
            if (sku.getCourseId() > 0 || sku.getStatus() != 1) continue;
            if (cheapestSku == null || sku.getPrice().compareTo(cheapestSku.getPrice()) < 0) cheapestSku = sku;
        }

        return cheapestSku;
    }

    public void setCheapestSku(SubjectSku cheapestSku) {
        this.cheapestSku = cheapestSku;
    }

    public boolean exists() {
        return id > 0;
    }

    public SubjectSku getSku(long skuId) {
        for (SubjectSku sku : skus) {
            if (sku.getId() == skuId) return sku;
        }

        return SubjectSku.NOT_EXIST_SUBJECT_SKU;
    }

    public static class Base extends Subject {
        public Base(Subject subject) {
            super();
            setId(subject.getId());
            setType(subject.getType());
            setTitle(subject.getTitle());
            setSubTitle(subject.getSubTitle());
            setCover(subject.getCover());
            setTags(subject.getTags());
            setPrice(subject.getPrice());
            setOriginalPrice(subject.getOriginalPrice());
            setAge(subject.getAge());
            setJoined(subject.getJoined());
            setScheduler(subject.getScheduler());
            setRegion(subject.getRegion());
            setStatus(subject.getStatus());
        }
    }
}
