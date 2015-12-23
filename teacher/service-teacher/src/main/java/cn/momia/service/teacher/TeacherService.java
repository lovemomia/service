package cn.momia.service.teacher;

import cn.momia.api.teacher.dto.Teacher;
import cn.momia.api.teacher.dto.TeacherStatus;

public interface TeacherService {
    TeacherStatus status(long userId);

    long add(Teacher teacher);
}
