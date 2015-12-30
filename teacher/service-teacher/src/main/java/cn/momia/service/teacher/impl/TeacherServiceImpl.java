package cn.momia.service.teacher.impl;

import cn.momia.api.teacher.dto.Material;
import cn.momia.api.teacher.dto.Student;
import cn.momia.common.service.AbstractService;
import cn.momia.service.teacher.TeacherService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TeacherServiceImpl extends AbstractService implements TeacherService {
    @Override
    public Material getMaterial(long userId, int materialId) {
        if (!accessable(userId, materialId)) return Material.NOT_EXIST_MATERIAL;

        Set<Integer> materialIds = Sets.newHashSet(materialId);
        List<Material> materials = listMaterials(materialIds);

        return materials.isEmpty() ? Material.NOT_EXIST_MATERIAL : materials.get(0);
    }

    private boolean accessable(long userId, int materialId) {
        String sql = "SELECT COUNT(DISTINCT B.Id) FROM SG_TeacherCourse A INNER JOIN SG_TeacherMaterial B ON A.CourseId=B.CourseId WHERE A.UserId=? AND B.Id=? AND A.Status<>0 AND B.Status<>0";
        return queryLong(sql, new Object[] { userId, materialId }) > 0;
    }

    @Override
    public long queryMaterialsCount(long userId) {
        String sql = "SELECT COUNT(DISTINCT B.Id) FROM SG_TeacherCourse A INNER JOIN SG_TeacherMaterial B ON A.CourseId=B.CourseId WHERE A.UserId=? AND A.Status<>0 AND B.Status<>0";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<Material> queryMaterials(long userId, int start, int count) {
        String sql = "SELECT B.Id FROM SG_TeacherCourse A INNER JOIN SG_TeacherMaterial B ON A.CourseId=B.CourseId WHERE A.UserId=? AND A.Status<>0 AND B.Status<>0 GROUP BY B.Id LIMIT ?,?";
        List<Integer> materialIds = queryIntList(sql, new Object[] { userId, start, count });

        return listMaterials(materialIds);
    }

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

    private List<Material> listMaterials(Collection<Integer> materialIds) {
        if (materialIds.isEmpty()) return new ArrayList<Material>();

        String sql = "SELECT A.Id, B.Cover, B.Title, C.Title AS Subject, A.Content FROM SG_TeacherMaterial A INNER JOIN SG_Course B ON A.CourseId=B.Id INNER JOIN SG_Subject C ON B.SubjectId=C.Id WHERE A.Id IN (" + StringUtils.join(materialIds, ",") + ") AND A.Status<>0 AND B.Status<>0 AND C.Status<>0";
        List<Material> materials = queryObjectList(sql, Material.class);

        Map<Integer, Material> materialsMap = new HashMap<Integer, Material>();
        for (Material material : materials) {
            materialsMap.put(material.getId(), material);
        }

        List<Material> result = new ArrayList<Material>();
        for (int materialId : materialIds) {
            Material material = materialsMap.get(materialId);
            if (material != null) result.add(material);
        }

        return result;
    }
}
