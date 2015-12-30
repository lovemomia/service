package cn.momia.service.user.teacher;

import cn.momia.api.user.dto.Teacher;
import cn.momia.api.user.dto.TeacherEducation;
import cn.momia.api.user.dto.TeacherExperience;
import cn.momia.api.user.dto.TeacherStatus;

public interface TeacherService {
    TeacherStatus status(long userId);
    Teacher getByUser(long userId);

    long add(Teacher teacher);

    boolean addExperience(long userId, TeacherExperience experience);
    TeacherExperience getExperience(long userId, int experienceId);
    boolean deleteExperience(long userId, int experienceId);

    boolean addEducation(long userId, TeacherEducation education);
    TeacherEducation getEducation(long userId, int educationId);
    boolean deleteEducation(long userId, int educationId);
}
