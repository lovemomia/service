package cn.momia.service.course.base;

import java.util.Date;

public class BookedCourse {
    public static final BookedCourse NOT_EXIST_BOOKED_COURSE = new BookedCourse();

    private long id;
    private long userId;
    private long orderId;
    private long packageId;
    private long courseId;
    private long courseSkuId;
    private Date startTime;
    private Date endTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getCourseSkuId() {
        return courseSkuId;
    }

    public void setCourseSkuId(long courseSkuId) {
        this.courseSkuId = courseSkuId;
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

    public boolean exists() {
        return id > 0;
    }

    public boolean canCancel() {
        return startTime.getTime() - new Date().getTime() > 2 * 24 * 60 * 60 * 1000;
    }
}
