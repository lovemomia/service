package cn.momia.service.course.subject;

import cn.momia.api.base.MetaUtil;
import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.util.TimeUtil;
import com.alibaba.fastjson.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Subject {
    public static class Type {
        public static final int NORMAL = 1;
        public static final int FREE = 2;
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("M月d日");

    public static final Subject NOT_EXIST_SUBJECT = new Subject();

    private long id;
    private int cityId;
    private int regionId;
    private String title;
    private String cover;
    private String tags;
    private int minAge;
    private int maxAge;
    private int joined;
    private String intro;
    private JSONArray notice;
    private Date startTime;
    private Date endTime;
    private List<String> imgs;
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

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public JSONArray getNotice() {
        return notice;
    }

    public void setNotice(JSONArray notice) {
        this.notice = notice;
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

    public String getScheduler() {
        if (TimeUtil.isSameDay(startTime, endTime)) {
            return DATE_FORMAT.format(startTime) + " " + TimeUtil.getWeekDay(startTime);
        } else {
            return DATE_FORMAT.format(startTime) + "-" + DATE_FORMAT.format(endTime);
        }
    }

    public String getRegion() {
        return MetaUtil.getRegionName(regionId);
    }
}
