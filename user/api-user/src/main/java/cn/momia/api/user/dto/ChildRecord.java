package cn.momia.api.user.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class ChildRecord {
    public static final ChildRecord EMPTY_RECORD = new ChildRecord();
    static {
        EMPTY_RECORD.setTags(new ArrayList<Integer>());
        EMPTY_RECORD.setContent("");
    }

    @JSONField(serialize = false) private long id;
    @JSONField(serialize = false) private long teacherUserId;
    @JSONField(serialize = false) private long childId;
    @JSONField(serialize = false) private long courseId;
    @JSONField(serialize = false) private long courseSkuId;
    private List<Integer> tags;
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

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
