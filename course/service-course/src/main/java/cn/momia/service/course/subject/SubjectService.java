package cn.momia.service.course.subject;

import java.util.Collection;
import java.util.List;

public interface SubjectService {
    Subject get(long subjectId);
    List<Subject> list(Collection<Long> subjectIds);

    long queryFreeCount(long cityId);
    List<Subject> queryFree(long cityId, int start, int count);

    SubjectSku getSku(long skuId);
    List<SubjectSku> listSkus(Collection<Long> skuIds);
    List<SubjectSku> querySkus(long subjectId);

    boolean isForNewUser(long subjectId);

    boolean isFavored(long userId, long subjectId);
    boolean favor(long userId, long subjectId);
    boolean unfavor(long userId, long subjectId);
}
