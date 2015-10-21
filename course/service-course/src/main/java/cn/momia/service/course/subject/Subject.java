package cn.momia.service.course.subject;

import java.util.List;

public class Subject {
    public static class Type {
        public static final int NORMAL = 1;
        public static final int FREE = 2;
    }

    public static final Subject NOT_EXIST_SUBJECT = new Subject();

    private long id;
    private int cityId;
    private String title;
    private String cover;
    private String tags;
    private String intro;
    private String notice;
    private List<SubjectImage> imgs;
    private List<SubjectSku> skus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<SubjectImage> getImgs() {
        return imgs;
    }

    public void setImgs(List<SubjectImage> imgs) {
        this.imgs = imgs;
    }

    public List<SubjectSku> getSkus() {
        return skus;
    }

    public void setSkus(List<SubjectSku> skus) {
        this.skus = skus;
    }

    public boolean exists() {
        return id > 0;
    }

    public SubjectSku getMinPriceSku() {
        SubjectSku minPriceSubjectSku = SubjectSku.NOT_EXIST_SUBJECT_SKU;
        for (SubjectSku sku : skus) {
            if (!minPriceSubjectSku.exists()) {
                minPriceSubjectSku = sku;
            } else {
                if (minPriceSubjectSku.getPrice().compareTo(sku.getPrice()) > 0) minPriceSubjectSku = sku;
            }
        }

        return minPriceSubjectSku;
    }

    public SubjectSku getSku(long skuId) {
        for (SubjectSku sku : skus) {
            if (sku.getId() == skuId) return sku;
        }

        return SubjectSku.NOT_EXIST_SUBJECT_SKU;
    }
}
