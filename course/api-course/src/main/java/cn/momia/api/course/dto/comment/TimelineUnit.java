package cn.momia.api.course.dto.comment;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class TimelineUnit {
    private long courseId;
    private String courseTitle;
    @JSONField(format = "yyyy-MM-dd") private Date time;
    private UserCourseComment comment;

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public UserCourseComment getComment() {
        return comment;
    }

    public void setComment(UserCourseComment comment) {
        this.comment = comment;
    }
}
