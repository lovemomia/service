package cn.momia.service.course.subject;

import java.util.Collection;
import java.util.List;

public interface SubjectService {
    Subject get(long subjectId);
    List<Subject> list(Collection<Long> subjectIds);

    long queryTrialCount(long cityId);
    List<Subject> queryTrial(long cityId, int start, int count);

    SubjectSku getSku(long skuId);
    List<SubjectSku> listSkus(Collection<Long> skuIds);
    List<SubjectSku> querySkus(long subjectId);

    boolean isTrial(long subjectId);
    boolean increaseStock(long subjectId, int count);
    boolean decreaseStock(long subjectId, int count);
}
