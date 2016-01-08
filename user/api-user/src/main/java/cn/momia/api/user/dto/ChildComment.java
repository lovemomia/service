package cn.momia.api.user.dto;

public class ChildComment {
    private long id;
    private long teacherUserId;
    private long childId;
    private long courseId;
    private long courseSkuId;
    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
