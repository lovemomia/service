package cn.momia.service.course.base;

import cn.momia.api.course.dto.Teacher;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CourseService {
    boolean isRecommended(long courseId);
    long queryRecommendCount(long cityId);
    List<Course> queryRecomend(long cityId, int start, int count);

    long queryTrialCount(long cityId);
    List<Course> queryTrial(long cityId, int start, int count);

    Course get(long courseId);
    List<Course> list(Collection<Long> courseIds);

    long queryBookImgCount(long courseId);
    List<String> queryBookImgs(long courseId, int start, int count);

    long queryTeacherCount(long courseId);
    List<Teacher> queryTeachers(long courseId, int start, int count);

    long queryCountBySubject(long subjectId, Collection<Long> exclusions, int minAge, int maxAge);
    List<Course> queryBySubject(long subjectId, int start, int count, Collection<Long> exclusions, int minAge, int maxAge, int sortTypeId);

    List<Course> queryAllBySubject(long subjectId);
    Map<Long, List<Course>> queryAllBySubjects(Collection<Long> subjectIds);

    List<CourseSku> querySkus(long courseId, String start, String end);
    CourseSku getSku(long skuId);

    boolean lockSku(long skuId);
    boolean unlockSku(long skuId);

    Map<Long, Date> queryStartTimesByPackages(Set<Long> packageIds);

    BookedCourse getBookedCourse(long bookingId);

    long listFinishedCount();
    List<Course> listFinished(int start, int count);
    long listFinishedCount(long userId);
    List<Course> listFinished(long userId, int start, int count);

    long queryNotFinishedCountByUser(long userId);
    List<BookedCourse> queryNotFinishedByUser(long userId, int start, int count);
    long queryFinishedCountByUser(long userId);
    List<BookedCourse> queryFinishedByUser(long userId, int start, int count);

    Map<Long, Integer> queryBookedCourseCounts(Set<Long> orderIds);
    Map<Long, Integer> queryFinishedCourseCounts(Set<Long> orderIds);

    List<Long> queryBookedCourseIds(long packageId);

    boolean booked(long packageId, long courseId);
    long booking(long userId, long orderId, long packageId, CourseSku sku);
    void increaseJoined(long courseId, int joinCount);
    boolean cancel(long userId, long bookingId);
    void decreaseJoined(long courseId, int joinCount);

    CourseDetail getDetail(long courseId);
    Institution getInstitution(long courseId);

    Map<Long, String> queryTips(Collection<Long> courseIds);

    boolean matched(long subjectId, long courseId);

    boolean joined(long userId, long courseId);

    boolean finished(long userId, long bookingId, long courseId);
    boolean isCommented(long userId, long bookingId);
    boolean comment(CourseComment comment);

    long queryCommentCountByCourse(long courseId);
    List<CourseComment> queryCommentsByCourse(long courseId, int start, int count);
    long queryCommentCountBySubject(long subjectId);
    List<CourseComment> queryCommentsBySubject(long subjectId, int start, int count);

    List<Long> queryCommentedBookingIds(long userId, Collection<Long> courseIds);
}
