package cn.momia.service.teacher.impl;

import cn.momia.api.teacher.dto.TeacherStatus;
import cn.momia.common.service.AbstractService;
import cn.momia.service.teacher.TeacherService;

public class TeacherServiceImpl extends AbstractService implements TeacherService {
    @Override
    public TeacherStatus status(long userId) {
        String sql = "SELECT Status, Msg FROM SG_TeacherCheck WHERE UserId=? AND Status<>0";
        return queryObject(sql, new Object[] { userId }, TeacherStatus.class, TeacherStatus.NOT_EXIST_TEACHER_STATUS);
    }
}
