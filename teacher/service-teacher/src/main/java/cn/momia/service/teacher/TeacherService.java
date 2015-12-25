package cn.momia.service.teacher;

import cn.momia.api.teacher.dto.ChildComment;
import cn.momia.api.teacher.dto.ChildTag;
import cn.momia.api.teacher.dto.Education;
import cn.momia.api.teacher.dto.Experience;
import cn.momia.api.teacher.dto.Material;
import cn.momia.api.teacher.dto.ChildRecord;
import cn.momia.api.teacher.dto.Teacher;
import cn.momia.api.teacher.dto.TeacherStatus;

import java.util.Date;
import java.util.List;

public interface TeacherService {
    TeacherStatus status(long userId);

    long add(Teacher teacher);

    Teacher getByUser(long userId);

    boolean updatePic(int teacherId, String pic);
    boolean updateName(int teacherId, String name);
    boolean updateIdNo(int teacherId, String idno);
    boolean updateSex(int teacherId, String sex);
    boolean updateBirthday(int teacherId, Date birthday);
    boolean updateAddress(int teacherId, String address);

    Material getMaterial(long userId, int materialId);
    long queryMaterialsCount(long userId);
    List<Material> queryMaterials(long userId, int start, int count);

    boolean checkin(long userId, long packageId, long courseId, long courseSkuId);

    long queryChildCommentsCount(long childId);
    List<ChildComment> queryChildComments(long childId, int start, int count);

    List<ChildTag> listTags();
    ChildRecord getRecord(long userId, long childId, long courseId, long courseSkuId);

    boolean record(ChildRecord childRecord);
    boolean comment(ChildComment childComment);

    boolean addExperience(long userId, Experience experience);
    Experience getExperience(long userId, int experienceId);
    boolean deleteExperience(long userId, int experienceId);
    boolean addEducation(long userId, Education education);
    Education getEducation(long userId, int educationId);
    boolean deleteEducation(long userId, int educationId);
}
