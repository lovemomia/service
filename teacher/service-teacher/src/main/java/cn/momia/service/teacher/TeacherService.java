package cn.momia.service.teacher;

import cn.momia.api.teacher.dto.Student;

import java.util.List;

public interface TeacherService {

    List<Long> queryUserIdsWithoutChild(long courseId, long courseSkuId);
    List<Student> queryAllStudents(long courseId, long courseSkuId);
    List<Student> queryCheckInStudents(long courseId, long courseSkuId);
    List<Long> queryCommentedChildIds(long courseId, long courseSkuId);

    boolean checkin(long userId, long packageId, long courseId, long courseSkuId);
}
