package cn.momia.api.course.dto.course;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Course implements Cloneable {
    public static final Course NOT_EXIST_COURSE = new Course();

    public static class ShowType {
        public static final int BASE = 1;
        public static final int FULL = 2;
    }

    public static class QueryType {
        public static final int NOT_END = 1;
        public static final int BOOKABLE = 2;
    }

    public static class Status {
        public static final int OK = 1;
        public static final int SOLD_OUT = 2;
    }

    private long id;
    private int type;
    private long parentId;
    private long subjectId;
    private String subject;
    private String title;
    private String keyWord;
    private String cover;
    @JSONField(serialize = false) private int minAge;
    @JSONField(serialize = false) private int maxAge;
    private boolean insurance;
    private int joined;
    private BigDecimal price;
    private BigDecimal originalPrice;
    @JSONField(serialize = false) private int stock;
    private int status;
    private boolean buyable;

    @JSONField(serialize = false) private List<CourseSku> skus = new ArrayList<CourseSku>();

    // 非数据库字段
    private String age;
    private String scheduler;
    @JSONField(serialize = false) private int regionId;
    private String region;

    private String goal;
    private String flow;
    private String tips;
    private String notice;
    private String subjectNotice;
    @JSONField(serialize = false) private int institutionId;
    private String institution;

    private List<String> imgs;
    private JSONObject book;
    private CourseSkuPlace place;

    private Date addTime;

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

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
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

    public boolean isInsurance() {
        return insurance;
    }

    public void setInsurance(boolean insurance) {
        this.insurance = insurance;
    }

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isBuyable() {
        return buyable;
    }

    public void setBuyable(boolean buyable) {
        this.buyable = buyable;
    }

    public List<CourseSku> getSkus() {
        return skus;
    }

    public void setSkus(List<CourseSku> skus) {
        this.skus = skus;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getSubjectNotice() {
        return subjectNotice;
    }

    public void setSubjectNotice(String subjectNotice) {
        this.subjectNotice = subjectNotice;
    }

    public int getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(int institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public JSONObject getBook() {
        return book;
    }

    public void setBook(JSONObject book) {
        this.book = book;
    }

    public CourseSkuPlace getPlace() {
        return place;
    }

    public void setPlace(CourseSkuPlace place) {
        this.place = place;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    @Override
    public Course clone() {
        try {
            return (Course) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists() {
        return id > 0;
    }

    @JSONField(serialize = false)
    public Date getStartTime() {
        Date now = new Date();
        List<Date> startTimes = new ArrayList<Date>();
        for (CourseSku sku : skus) {
            if (!sku.isEnded(now)) startTimes.add(sku.getStartTime());
        }
        Collections.sort(startTimes);

        return startTimes.isEmpty() ? null : startTimes.get(0);
    }

    @JSONField(serialize = false)
    public Date getEndTime() {
        Date now = new Date();
        List<Date> endTimes = new ArrayList<Date>();
        for (CourseSku sku : skus) {
            if (!sku.isEnded(now)) endTimes.add(sku.getEndTime());
        }
        Collections.sort(endTimes);

        return endTimes.isEmpty() ? null : endTimes.get(endTimes.size() - 1);
    }

    public String getScheduler(long skuId) {
        for (CourseSku sku : skus) {
            if (sku.getId() == skuId) return sku.getScheduler();
        }

        return "";
    }

    public CourseSkuPlace getPlace(long skuId) {
        for (CourseSku sku : skus) {
            if (sku.getId() == skuId) return sku.getPlace();
        }

        return null;
    }

    public static class Base extends Course {
        protected Base() {

        }

        public Base(Course course) {
            setId(course.getId());
            setParentId(course.getParentId());
            setType(course.getType());
            setSubjectId(course.getSubjectId());
            setSubject(course.getSubject());
            setTitle(course.getTitle());
            setKeyWord(course.getKeyWord());
            setCover(course.getCover());
            setAge(course.getAge());
            setInsurance(course.isInsurance());
            setJoined(course.getJoined());
            setPrice(course.getPrice());
            setOriginalPrice(course.getOriginalPrice());
            setScheduler(course.getScheduler());
            setRegion(course.getRegion());
            setStatus(course.getStatus());
            setBuyable(course.isBuyable());
            setGoal(course.getGoal());
            setAddTime(course.getAddTime());
        }
    }
}
