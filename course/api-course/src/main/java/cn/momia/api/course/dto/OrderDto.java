package cn.momia.api.course.dto;

import java.math.BigDecimal;

public class OrderDto {
    private long id;
    private long subjectId;
    private long skuId;
    private int count;
    private BigDecimal totalFee;

    private Integer totalCourseCount;
    private Integer BookedCourseCount;

    private String title;
    private String cover;

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

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getTotalCourseCount() {
        return totalCourseCount;
    }

    public void setTotalCourseCount(Integer totalCourseCount) {
        this.totalCourseCount = totalCourseCount;
    }

    public Integer getBookedCourseCount() {
        return BookedCourseCount;
    }

    public void setBookedCourseCount(Integer bookedCourseCount) {
        BookedCourseCount = bookedCourseCount;
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
}
