package cn.momia.service.course.base;

public class CourseDetail {
    public static final CourseDetail NOT_EXIST_COURSE_DETAIL = new CourseDetail();

    private long id;
    private long courseId;
    private String abstracts;
    private String detail;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean exists() {
        return id > 0;
    }
}
