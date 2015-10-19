package cn.momia.service.course.subject;

import cn.momia.common.service.Entity;

import java.math.BigDecimal;

public class SubjectSku implements Entity, Cloneable {
    public static final SubjectSku NOT_EXIST_SUBJECT_SKU = new SubjectSku();

    private long id;
    private long subjectId;
    private String desc;
    private BigDecimal price = new BigDecimal(0);
    private BigDecimal originalPrice = new BigDecimal(0);
    private int adult;
    private int child;
    private int courseCount;
    private int time;
    private int timeUnit;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public boolean exists() {
        return id > 0;
    }

    public int getJoinCount() {
        return adult + child;
    }

    @Override
    public SubjectSku clone() {
        try {
            return (SubjectSku) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
