package cn.momia.service.course.material.impl;

import cn.momia.api.course.dto.material.CourseMaterial;
import cn.momia.common.service.AbstractService;
import cn.momia.service.course.material.CourseMaterialService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseMaterialServiceImpl extends AbstractService implements CourseMaterialService {
    @Override
    public CourseMaterial getMaterial(long userId, int materialId) {
        if (!accessable(userId, materialId)) return CourseMaterial.NOT_EXIST_COURSE_MATERIAL;

        Set<Integer> materialIds = Sets.newHashSet(materialId);
        List<CourseMaterial> materials = listMaterials(materialIds);

        return materials.isEmpty() ? CourseMaterial.NOT_EXIST_COURSE_MATERIAL : materials.get(0);
    }

    private List<CourseMaterial> listMaterials(Collection<Integer> materialIds) {
        if (materialIds.isEmpty()) return new ArrayList<CourseMaterial>();

        String sql = "SELECT A.Id, B.Cover, B.Title, C.Title AS Subject, A.Content FROM SG_CourseMaterial A INNER JOIN SG_Course B ON A.CourseId=B.Id INNER JOIN SG_Subject C ON B.SubjectId=C.Id WHERE A.Id IN (" + StringUtils.join(materialIds, ",") + ") AND A.Status<>0 AND B.Status<>0 AND C.Status<>0";
        List<CourseMaterial> materials = queryObjectList(sql, CourseMaterial.class);

        Map<Integer, CourseMaterial> materialsMap = new HashMap<Integer, CourseMaterial>();
        for (CourseMaterial material : materials) {
            materialsMap.put(material.getId(), material);
        }

        List<CourseMaterial> result = new ArrayList<CourseMaterial>();
        for (int materialId : materialIds) {
            CourseMaterial material = materialsMap.get(materialId);
            if (material != null) result.add(material);
        }

        return result;
    }

    private boolean accessable(long userId, int materialId) {
        String sql = "SELECT COUNT(DISTINCT B.Id) FROM SG_CourseTeacher A INNER JOIN SG_CourseMaterial B ON A.CourseId=B.CourseId WHERE A.UserId=? AND B.Id=? AND A.Status<>0 AND B.Status<>0";
        return queryLong(sql, new Object[] { userId, materialId }) > 0;
    }

    @Override
    public long queryMaterialsCount(long userId) {
        String sql = "SELECT COUNT(DISTINCT B.Id) FROM SG_CourseTeacher A INNER JOIN SG_CourseMaterial B ON A.CourseId=B.CourseId WHERE A.UserId=? AND A.Status<>0 AND B.Status<>0";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<CourseMaterial> queryMaterials(long userId, int start, int count) {
        String sql = "SELECT B.Id FROM SG_CourseTeacher A INNER JOIN SG_CourseMaterial B ON A.CourseId=B.CourseId WHERE A.UserId=? AND A.Status<>0 AND B.Status<>0 GROUP BY B.Id LIMIT ?,?";
        List<Integer> materialIds = queryIntList(sql, new Object[] { userId, start, count });

        return listMaterials(materialIds);
    }
}
