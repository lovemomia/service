package cn.momia.api.course.dto;

public class BookedCourseDto extends CourseDto {
    private long bookingId;
    private long courseSkuId;
    private boolean commented;

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public long getCourseSkuId() {
        return courseSkuId;
    }

    public void setCourseSkuId(long courseSkuId) {
        this.courseSkuId = courseSkuId;
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
}
