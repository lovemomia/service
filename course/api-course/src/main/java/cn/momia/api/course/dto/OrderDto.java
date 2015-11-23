package cn.momia.api.course.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class OrderDto {
    private long id;
    private int count;
    private BigDecimal totalFee;

    private int status;
    private Date addTime;

    private Integer bookingStatus;

    // 课程体系的内容
    private long subjectId;
    private long courseId;
    private String title;
    private String cover;

    // 使用的红包/优惠券
    private long userCouponId;
    private int couponId;
    private int couponType;
    private BigDecimal discount;
    private String couponDesc;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(Integer bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
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

    public long getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(long userCouponId) {
        this.userCouponId = userCouponId;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public int getCouponType() {
        return couponType;
    }

    public void setCouponType(int couponType) {
        this.couponType = couponType;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getCouponDesc() {
        return couponDesc;
    }

    public void setCouponDesc(String couponDesc) {
        this.couponDesc = couponDesc;
    }
}
