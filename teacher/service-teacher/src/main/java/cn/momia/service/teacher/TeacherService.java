package cn.momia.service.teacher;

import cn.momia.api.teacher.dto.ChildComment;
import cn.momia.api.teacher.dto.Material;
import cn.momia.api.teacher.dto.Record;
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

    long queryChildCommentsCount(long childId);
    List<ChildComment> queryChildComments(long childId, int start, int count);

    boolean record(Record record);
    boolean comment(ChildComment childComment);
}
