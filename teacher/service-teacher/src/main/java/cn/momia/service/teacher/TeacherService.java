package cn.momia.service.teacher;

import cn.momia.api.teacher.dto.TeacherStatus;

public interface TeacherService {
    TeacherStatus status(long userId);
}
