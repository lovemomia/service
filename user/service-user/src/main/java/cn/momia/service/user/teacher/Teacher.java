package cn.momia.service.user.teacher;

import cn.momia.common.core.util.MomiaUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class Teacher {
    private int id;
    private long userId;
    private String pic = "";
    private String name = "";
    private String idNo = "";
    private String sex = "";
    private Date birthday;
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
        return StringUtils.isBlank(experience) ? (experiences != null && experiences.size() > 0 ? StringUtils.join(experiences, "\n") : "") : experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEducation() {
        return StringUtils.isBlank(education) ? (educations != null && educations.size() > 0 ? educations.get(0).toString() : "") : education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public boolean exists() {
        return id > 0;
    }

    public boolean isInvalid() {
        return userId <= 0 ||
                StringUtils.isBlank(pic) ||
                StringUtils.isBlank(name) ||
                StringUtils.isBlank(idNo) ||
                MomiaUtil.isInvalidSex(sex) ||
                birthday == null ||
                StringUtils.isBlank(address);
    }

    public boolean isCompleted() {
        return !(isInvalid() || experiences == null | experiences.isEmpty() || educations == null || educations.isEmpty());
    }

    public String getAvatar() {
        return pic;
    }

    public static class Base extends Teacher {
        public Base(Teacher teacher) {
            super();
            setId(teacher.getId());
            setUserId(teacher.getUserId());
            setPic(teacher.getPic());
            setName(teacher.getName());

            setExperience(teacher.getExperience());
            setEducation(teacher.getEducation());
        }
    }
}
