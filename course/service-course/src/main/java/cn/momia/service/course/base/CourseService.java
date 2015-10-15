package cn.momia.service.course.base;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CourseService {
    Course get(long id);
    List<Course> list(Collection<Long> ids);

    long queryCountBySubject(int subjectId);
    List<Course> queryBySubject(int subjectId, int start, int count);

    List<Course> queryAllBySubject(long subjectId);
    Map<Long, List<Course>> queryAllBySubjects(Collection<Long> subjectIds);

    List<CourseSku> querySkus(long id, String start, String end);

    long queryNotFinishedSkuCountByUser(long userId);
    List<CourseSku> queryNotFinishedSkuByUser(long userId);
    long queryFinishedSkuCountByUser(long userId);
    List<CourseSku> queryFinishedSkuByUser(long userId);

    Map<Long, Integer> queryBookedCourseCounts(Set<Long> orderIds);
    Map<Long, Integer> queryFinishedCourseCounts(Set<Long> orderIds);
}
