package cn.momia.service.teacher;

import cn.momia.api.teacher.dto.Teacher;
import cn.momia.api.teacher.dto.TeacherStatus;

import java.util.Date;

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
}
