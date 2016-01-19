package cn.momia.api.user.dto;

import cn.momia.common.core.util.SexUtil;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class Teacher {
    public static final Teacher NOT_EXIST_TEACHER = new Teacher();

    private int id;
    private long userId;
    private String pic = "";
    private String name = "";
    private String idNo = "";
    private String sex = "";
    @JSONField(format = "yyyy-MM-dd") private Date birthday;
    private String address = "";

    private List<TeacherExperience> experiences;
    private List<TeacherEducation> educations;

    // 为了兼容老的
    private String experience;
    private String education;

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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<TeacherExperience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<TeacherExperience> experiences) {
        this.experiences = experiences;
    }

    public List<TeacherEducation> getEducations() {
        return educations;
    }

    public void setEducations(List<TeacherEducation> educations) {
        this.educations = educations;
    }

    public String getExperience() {
        return StringUtils.isBlank(experience) ? StringUtils.join(experiences, "\n") : experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEducation() {
        return StringUtils.isBlank(education) ? StringUtils.join(educations, "\n") : education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public boolean exists() {
        return id > 0;
    }

    @JSONField(serialize = false)
    public boolean isInvalid() {
        return userId <= 0 ||
                StringUtils.isBlank(pic) ||
                StringUtils.isBlank(name) ||
                StringUtils.isBlank(idNo) ||
                SexUtil.isInvalid(sex) ||
                birthday == null ||
                StringUtils.isBlank(address);
    }

    @JSONField(serialize = false)
    public boolean isCompleted() {
        return !(isInvalid() || experiences == null | experiences.isEmpty() || educations == null || educations.isEmpty());
    }

    public String getAvatar() {
        return pic;
    }
}
