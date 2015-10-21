package cn.momia.service.course.base;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CourseService {
    Course get(long courseId);
    List<Course> list(Collection<Long> courseIds);

    long queryBookImgCount(long courseId);
    List<String> queryBookImgs(long courseId, int start, int count);

    long queryTeacherCount(long courseId);
    List<Teacher> queryTeachers(long courseId, int start, int count);

    long queryCountBySubject(int subjectId);
    List<Course> queryBySubject(int subjectId, int start, int count);

    List<Course> queryAllBySubject(long subjectId);
    Map<Long, List<Course>> queryAllBySubjects(Collection<Long> subjectIds);

    List<CourseSku> querySkus(long courseId, String start, String end);
    CourseSku getSku(long skuId);

    boolean lockSku(long skuId);
    boolean unlockSku(long skuId);

    long queryNotFinishedCountByUser(long userId);
    List<Course> queryNotFinishedByUser(long userId, int start, int count);
    long queryFinishedCountByUser(long userId);
    List<Course> queryFinishedByUser(long userId, int start, int count);

    Map<Long, Integer> queryBookedCourseCounts(Set<Long> orderIds);
    Map<Long, Integer> queryFinishedCourseCounts(Set<Long> orderIds);

    long booking(long userId, long orderId, long packageId, CourseSku sku);
    void increaseJoined(long courseId, int joinCount);

    boolean isFavored(long userId, long courseId);
    boolean favor(long userId, long courseId);
    boolean unfavor(long userId, long courseId);

    CourseDetail getDetail(long courseId);
    Institution getInstitution(long courseId);

    boolean matched(long subjectId, long courseId);
}
