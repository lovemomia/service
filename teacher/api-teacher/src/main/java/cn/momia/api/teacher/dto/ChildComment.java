package cn.momia.api.teacher.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class ChildComment {
    @JSONField(format = "yyyy-MM-dd") private Date date;
    private String title;
    private String content;
    private String teacher;

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
