package cn.momia.service.teacher;

import cn.momia.api.teacher.dto.Education;
import cn.momia.api.teacher.dto.Experience;
import cn.momia.api.teacher.dto.Material;
import cn.momia.api.teacher.dto.Student;
import cn.momia.api.teacher.dto.Teacher;
import cn.momia.api.teacher.dto.TeacherStatus;

import java.util.List;

public interface TeacherService {
    TeacherStatus status(long userId);
    Teacher getByUser(long userId);

    long add(Teacher teacher);

    Material getMaterial(long userId, int materialId);
    long queryMaterialsCount(long userId);
    List<Material> queryMaterials(long userId, int start, int count);

    List<Long> queryUserIdsWithoutChild(long courseId, long courseSkuId);
    List<Student> queryAllStudents(long courseId, long courseSkuId);
    List<Student> queryCheckInStudents(long courseId, long courseSkuId);
    List<Long> queryCommentedChildIds(long courseId, long courseSkuId);

    boolean checkin(long userId, long packageId, long courseId, long courseSkuId);

    boolean addExperience(long userId, Experience experience);
    Experience getExperience(long userId, int experienceId);
    boolean deleteExperience(long userId, int experienceId);
    boolean addEducation(long userId, Education education);
    Education getEducation(long userId, int educationId);
    boolean deleteEducation(long userId, int educationId);
}
