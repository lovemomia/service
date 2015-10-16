package cn.momia.api.course.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class OrderDto {
    private long id;
    private int count;
    private BigDecimal totalFee;

    private Date addTime;

    private Integer totalCourseCount;
    private Integer bookedCourseCount;
    private Integer finishedCourseCount;

    // 课程体系的内容
    private String title;
    private String cover;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getTotalCourseCount() {
        return totalCourseCount;
    }

    public void setTotalCourseCount(Integer totalCourseCount) {
        this.totalCourseCount = totalCourseCount;
    }

    public Integer getBookedCourseCount() {
        return bookedCourseCount;
    }

    public void setBookedCourseCount(Integer bookedCourseCount) {
        this.bookedCourseCount = bookedCourseCount;
    }

    public Integer getFinishedCourseCount() {
        return finishedCourseCount;
    }

    public void setFinishedCourseCount(Integer finishedCourseCount) {
        this.finishedCourseCount = finishedCourseCount;
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
