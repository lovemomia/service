package cn.momia.api.course.dto;

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
    private int cityId;
    private String title;
    private String cover;
    private String tags;
    private String intro;
    private String notice;
    private int stock;
    private List<String> imgs;
    private List<SubjectSku> skus;

    private int status;

    private BigDecimal price;
    private BigDecimal originalPrice;
    private String age;
    private int joined;
    private String scheduler;
    private String region;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JSONField(serialize = false)
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @JSONField(serialize = false)
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

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    @JSONField(serialize = false)
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
            setTitle(subject.getTitle());
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
