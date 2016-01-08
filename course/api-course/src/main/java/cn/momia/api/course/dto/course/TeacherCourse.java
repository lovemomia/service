package cn.momia.api.course.dto.course;

import com.alibaba.fastjson.annotation.JSONField;

public class TeacherCourse {
    public static final TeacherCourse NOT_EXIST_TEACHER_COURSE = new TeacherCourse();

    @JSONField(serialize = false) private long teacherCourseId;
    private long courseId;
    private long courseSkuId;
    private String cover;
    private String title;
    private String scheduler;
    private String address;

    private boolean commented;

    public long getTeacherCourseId() {
        return teacherCourseId;
    }

    public void setTeacherCourseId(long teacherCourseId) {
        this.teacherCourseId = teacherCourseId;
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

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isCommented() {
        return commented;
    }

    public void setCommented(boolean commented) {
        this.commented = commented;
    }

    public boolean exists() {
        return courseId > 0 && courseSkuId > 0;
    }
}
