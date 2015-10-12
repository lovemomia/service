package cn.momia.service.course.subject;

import cn.momia.common.api.exception.MomiaFailedException;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class Subject {
    public static final Subject NOT_EXIST_SUBJECT = new Subject();

    private long id;
    private String title;
    private String cover;
    private int minAge;
    private int maxAge;
    private int joined;
    private JSONArray intro;
    private JSONArray notice;
    private List<String> imgs;
    private List<SubjectSku> skus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public JSONArray getIntro() {
        return intro;
    }

    public void setIntro(JSONArray intro) {
        this.intro = intro;
    }

    public JSONArray getNotice() {
        return notice;
    }

    public void setNotice(JSONArray notice) {
        this.notice = notice;
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

    public boolean exists() {
        return id > 0;
    }

    public SubjectSku getMinPriceSku() {
        SubjectSku minPriceSubjectSku = null;
        for (SubjectSku sku : skus) {
            if (minPriceSubjectSku == null) {
                minPriceSubjectSku = sku;
            } else {
                if (minPriceSubjectSku.getPrice().compareTo(sku.getPrice()) > 0) minPriceSubjectSku = sku;
            }
        }

        return minPriceSubjectSku;
    }

    public String getAge() {
        if (minAge <= 0 && maxAge <= 0) throw new MomiaFailedException("invalid age of subject sku: " + id);
        if (minAge <= 0) return maxAge + "岁";
        if (maxAge <= 0) return minAge + "岁";
        if (minAge == maxAge) return minAge + "岁";
        return minAge + "-" + maxAge + "岁";
    }
}
