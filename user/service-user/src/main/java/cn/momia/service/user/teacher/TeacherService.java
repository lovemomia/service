package cn.momia.service.user.teacher;

import cn.momia.api.user.dto.Teacher;
import cn.momia.api.user.dto.TeacherEducation;
import cn.momia.api.user.dto.TeacherExperience;
import cn.momia.api.user.dto.TeacherStatus;

import java.util.Collection;
import java.util.List;

public interface TeacherService {
    TeacherStatus status(long userId);
    Teacher getByUser(long userId);
    List<Teacher> list(Collection<Integer> teacherIds);
    List<Teacher> listByUser(Collection<Long> teacherUserIds);

    long add(Teacher teacher);

    boolean addExperience(long userId, TeacherExperience experience);
    TeacherExperience getExperience(long userId, int experienceId);
    boolean deleteExperience(long userId, int experienceId);
    List<TeacherExperience> listExperiences(long userId);

    boolean addEducation(long userId, TeacherEducation education);
    TeacherEducation getEducation(long userId, int educationId);
    boolean deleteEducation(long userId, int educationId);
    List<TeacherEducation> listEducations(long userId);
}
