package cn.momia.service.course.subject;

import cn.momia.api.course.dto.subject.Subject;
import cn.momia.api.course.dto.subject.SubjectSku;

import java.util.Collection;
import java.util.List;

public interface SubjectService {
    Subject get(long subjectId);
    List<Subject> list(Collection<Long> subjectIds);
    List<Subject> list(int cityId);

    SubjectSku getSku(long skuId);
    List<SubjectSku> listSkus(Collection<Long> skuIds);
    List<SubjectSku> querySkus(long subjectId);

    boolean isTrial(long subjectId);

    long queryTrialCount(long cityId);
    List<Subject> queryTrial(long cityId, int start, int count);

    boolean increaseStock(long subjectId, int count);
    boolean decreaseStock(long subjectId, int count);
}
