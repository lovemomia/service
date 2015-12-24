package cn.momia.api.teacher.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class ChildComment {
    @JSONField(serialize = false) private long id;
    @JSONField(serialize = false) private long teacherUserId;
    @JSONField(serialize = false) private long childId;
    @JSONField(serialize = false) private long courseId;
    @JSONField(serialize = false) private long courseSkuId;

    @JSONField(format = "yyyy-MM-dd") private Date date;
    private String title;
    private String content;
    private String teacher;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTeacherUserId() {
        return teacherUserId;
    }

    public void setTeacherUserId(long teacherUserId) {
        this.teacherUserId = teacherUserId;
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
