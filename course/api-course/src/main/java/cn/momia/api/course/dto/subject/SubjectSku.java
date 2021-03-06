package cn.momia.api.course.dto.subject;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class SubjectSku implements Cloneable {
    public static final SubjectSku NOT_EXIST_SUBJECT_SKU = new SubjectSku();

    private long id;
    private String cover;
    private String title;
    private long subjectId;
    private String desc;
    private BigDecimal price = new BigDecimal(0);
    @JSONField(serialize = false) private BigDecimal originalPrice = new BigDecimal(0);
    private int adult;
    private int child;
    private int courseCount;
    private int time;
    private int timeUnit;
    private int limit;
    private int status;

    private int count;
    private long courseId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = courseCount;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public boolean exists() {
        return id > 0;
    }

    @JSONField(serialize = false)
    public int getJoinCount() {
        return adult + child;
    }

    @JSONField(serialize = false)
    public boolean isAvaliable() {
        return status == 1;
    }

    @Override
    public SubjectSku clone() {
        try {
            return (SubjectSku) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return price.intValue() + "元 " + desc;
    }
}
