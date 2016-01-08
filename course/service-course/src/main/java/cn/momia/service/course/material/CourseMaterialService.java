package cn.momia.service.course.material;

import cn.momia.api.course.dto.material.CourseMaterial;

import java.util.List;

public interface CourseMaterialService {
    CourseMaterial getMaterial(long userId, int materialId);
    long queryMaterialsCount(long userId);
    List<CourseMaterial> queryMaterials(long userId, int start, int count);
}
