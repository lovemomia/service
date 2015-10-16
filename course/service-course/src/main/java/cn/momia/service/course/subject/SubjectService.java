package cn.momia.service.course.subject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SubjectService {
    Subject get(long id);
    List<Subject> list(Collection<Long> ids);

    long queryFreeCount(long cityId);
    List<Subject> queryFree(long cityId, int start, int count);

    SubjectSku getSku(long skuId);
    List<SubjectSku> listSkus(Collection<Long> skuIds);
    List<SubjectSku> querySkus(long id);
}
