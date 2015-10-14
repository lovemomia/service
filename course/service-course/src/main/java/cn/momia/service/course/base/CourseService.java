package cn.momia.service.course.base;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CourseService {
    Course get(long id);
    List<Course> list(Collection<Long> ids);

    long queryRecommendCount(int cityId);
    List<Course> queryRecommend(int cityId, int start, int count);

    long queryCountBySubject(int subjectId);
    List<Course> queryBySubject(int subjectId, int start, int count);

    List<CourseSku> querySkus(long id, String start, String end);

    Map<Long,Integer> queryBookedCourseCounts(Set<Long> orderIds);
    Map<Long,Integer> queryFinishedCourseCounts(Set<Long> orderIds);
}
