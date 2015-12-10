package cn.momia.api.course.dto;

import cn.momia.common.util.TimeUtil;
import com.alibaba.fastjson.annotation.JSONField;

import java.text.ParseException;
import java.util.Date;

public class BookedCourse extends Course.Base {
    public static final BookedCourse NOT_EXIST_BOOKED_COURSE = new BookedCourse();

    private long bookingId;
    @JSONField(serialize = false) private long userId;
    @JSONField(serialize = false) private long orderId;
    @JSONField(serialize = false) private long packageId;
    @JSONField(serialize = false) private long courseId;
    private long courseSkuId;
    private long parentCourseSkuId;
    @JSONField(serialize = false) private Date startTime;
    @JSONField(serialize = false) private Date endTime;
    private boolean commented;

    public BookedCourse() {

    }

    public BookedCourse(Course course) {
        super(course);
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
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

    public long getParentCourseSkuId() {
        return parentCourseSkuId;
    }

    public void setParentCourseSkuId(long parentCourseSkuId) {
        this.parentCourseSkuId = parentCourseSkuId;
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

    public boolean isCommented() {
        return commented;
    }

    public void setCommented(boolean commented) {
        this.commented = commented;
    }

    public boolean exists() {
        return bookingId > 0;
    }

    public boolean canCancel() {
        try {
            Date startDate = TimeUtil.SHORT_DATE_FORMAT.parse(TimeUtil.SHORT_DATE_FORMAT.format(startTime));
            return startDate.getTime() - new Date().getTime() > 2 * 24 * 60 * 60 * 1000;
        } catch (ParseException e) {
            return false;
        }
    }
}
