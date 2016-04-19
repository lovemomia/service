package cn.momia.service.course.subject;

import cn.momia.api.course.dto.subject.Subject;
import cn.momia.api.course.dto.subject.SubjectSku;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SubjectService {
    Subject get(long subjectId);
    List<Subject> list(Collection<Long> subjectIds);
    List<Subject> list(int cityId);

    SubjectSku getSku(long skuId);
    List<SubjectSku> listSkus(Collection<Long> skuIds);
    List<SubjectSku> querySkus(long subjectId);
    Map<Long, List<SubjectSku>> querySkus(Collection<Long> subjectIds);

    boolean isTrial(long subjectId);

    boolean increaseStock(long subjectId, int count);
    boolean decreaseStock(long subjectId, int count);
}
