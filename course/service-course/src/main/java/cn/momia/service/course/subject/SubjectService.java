package cn.momia.service.course.subject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SubjectService {
    Subject get(long id);
    List<SubjectSku> listSkus(long id);
    Map<Long, String> queryTitlesByCourse(Set<Long> courseIds);
}
