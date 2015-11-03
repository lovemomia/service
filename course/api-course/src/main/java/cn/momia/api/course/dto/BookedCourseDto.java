package cn.momia.api.course.dto;

public class BookedCourseDto extends CourseDto {
    private long bookingId;

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }
}
