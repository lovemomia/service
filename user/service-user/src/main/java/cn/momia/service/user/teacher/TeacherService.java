package cn.momia.service.user.teacher;

import java.util.Collection;
import java.util.List;

public interface TeacherService {
    TeacherStatus status(long userId);
    Teacher getByUser(long userId);
    List<Teacher> list(Collection<Integer> teacherIds);
    List<Teacher> listByUser(Collection<Long> teacherUserIds);

    long add(Teacher teacher);

    int addExperience(long userId, TeacherExperience experience);
    TeacherExperience getExperience(long userId, int experienceId);
    boolean deleteExperience(long userId, int experienceId);
    List<TeacherExperience> listExperiences(long userId);

    int addEducation(long userId, TeacherEducation education);
    TeacherEducation getEducation(long userId, int educationId);
    boolean deleteEducation(long userId, int educationId);
    List<TeacherEducation> listEducations(long userId);
}
