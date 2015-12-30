package cn.momia.api.user.dto;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

public class TeacherExperience {
    public static final TeacherExperience NOT_EXIST_TEACHER_EXPERIENCE = new TeacherExperience();

    private int id;
    private long userId;
    private String school;
    private String post;
    private String time;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean exists() {
        return id > 0;
    }

    @JSONField(serialize = false)
    public boolean isInvalid() {
        return StringUtils.isBlank(school) || StringUtils.isBlank(post) || StringUtils.isBlank(time) || StringUtils.isBlank(content);
    }
}
