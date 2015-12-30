package cn.momia.service.teacher.impl;

import cn.momia.api.teacher.dto.Student;
import cn.momia.common.service.AbstractService;
import cn.momia.service.teacher.TeacherService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherServiceImpl extends AbstractService implements TeacherService {
    @Override
    public List<Long> queryUserIdsWithoutChild(long courseId, long courseSkuId) {
        String sql = "SELECT UserId FROM SG_BookedCourse WHERE CourseId=? AND CourseSkuId=? AND Status<>0 AND ChildId=0";
        return queryLongList(sql, new Object[] { courseId, courseSkuId });
    }

    @Override
    public List<Student> queryAllStudents(long courseId, long courseSkuId) {
        String sql = "SELECT B.Id FROM SG_BookedCourse A INNER JOIN SG_Child B ON A.ChildId=B.Id WHERE A.CourseId=? AND A.CourseSkuId=? AND A.Status<>0 AND B.Status<>0";
        List<Long> childIds = queryLongList(sql, new Object[] { courseId, courseSkuId });

        return listStudents(childIds, courseId, courseSkuId);
    }

    private List<Student> listStudents(List<Long> childIds, long courseId, long courseSkuId) {
        if (childIds.isEmpty()) return new ArrayList<Student>();

        String sql = "SELECT B.Id, B.UserId, B.Avatar, B.Name, B.Birthday, B.Sex, A.PackageId, A.CheckIn FROM SG_BookedCourse A INNER JOIN SG_Child B ON A.ChildId=B.Id WHERE A.CourseId=? AND A.CourseSkuId=? AND A.Status<>0 AND B.Id IN (" + StringUtils.join(childIds, ",") + ") AND B.Status<>0";
        List<Student> students = queryObjectList(sql, new Object[] {courseId, courseSkuId  }, Student.class);
        Map<Long, Student> studentsMap = new HashMap<Long, Student>();
        for (Student student : students) {
            studentsMap.put(student.getId(), student);
        }

        List<Student> result = new ArrayList<Student>();
        for (long childId : childIds) {
            Student student = studentsMap.get(childId);
            if (student != null) result.add(student);
        }

        return result;
    }

    @Override
    public List<Student> queryCheckInStudents(long courseId, long courseSkuId) {
        String sql = "SELECT B.Id FROM SG_BookedCourse A INNER JOIN SG_Child B ON A.ChildId=B.Id WHERE A.CourseId=? AND A.CourseSkuId=? AND A.CheckIn>0 AND A.Status<>0 AND B.Status<>0";
        List<Long> childIds = queryLongList(sql, new Object[] { courseId, courseSkuId });

        return listStudents(childIds, courseId, courseSkuId);
    }

    @Override
    public List<Long> queryCommentedChildIds(long courseId, long courseSkuId) {
        String sql = "SELECT ChildId FROM SG_ChildComment WHERE CourseId=? AND CourseSkuId=? AND Status<>0";
        return queryLongList(sql, new Object[] { courseId, courseSkuId });
    }

    @Override
    public boolean checkin(long userId, long packageId, long courseId, long courseSkuId) {
        String sql = "UPDATE SG_BookedCourse SET CheckIn=1 WHERE UserId=? AND PackageId=? AND CourseId=? AND CourseSkuId=? AND Status<>0";
        return update(sql, new Object[] { userId, packageId, courseId, courseSkuId });
    }
}
