package cn.momia.api.course.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class TimelineUnit {
    private long courseId;
    private String courseTitle;
    @JSONField(format = "yyyy-MM-dd") private Date startTime;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public UserCourseComment getComment() {
        return comment;
    }

    public void setComment(UserCourseComment comment) {
        this.comment = comment;
    }
}
