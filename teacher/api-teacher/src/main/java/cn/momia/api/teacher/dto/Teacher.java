package cn.momia.api.teacher.dto;

import cn.momia.common.util.SexUtil;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class Teacher {
    public static final Teacher NOT_EXIST_TEACHER = new Teacher();

    private int id;
    @JSONField(serialize = false) private long userId;
    private String pic;
    private String name;
    private String idNo;
    private String sex;
    @JSONField(format = "yyyy-MM-dd") private Date birthday;
    private String address;

    private List<Experience> experiences;
    private List<Education> educations;

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

    public List<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }

    public List<Education> getEducations() {
        return educations;
    }

    public void setEducations(List<Education> educations) {
        this.educations = educations;
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
}
