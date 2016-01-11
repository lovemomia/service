package cn.momia.service.course.base;

import cn.momia.api.course.dto.course.BookedCourse;
import cn.momia.api.course.dto.course.Course;
import cn.momia.api.course.dto.course.CourseDetail;
import cn.momia.api.course.dto.course.CourseSku;
import cn.momia.api.course.dto.course.Student;
import cn.momia.api.course.dto.course.TeacherCourse;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CourseService {
    boolean isRecommended(long courseId);
    long queryRecommendCount(long cityId);
    List<Course> queryRecomend(long cityId, int start, int count);

    long queryTrialCount(long cityId);
    List<Course> queryTrial(long cityId, int start, int count);

    Course get(long courseId);
    List<Course> list(Collection<Long> courseIds);
    List<CourseSku> listSkus(Collection<Long> skuIds);

    long queryBookImgCount(long courseId);
    List<String> queryBookImgs(long courseId, int start, int count);

    long queryTeacherIdsCount(long courseId);
    List<Integer> queryTeacherIds(long courseId, int start, int count);

    long queryCountBySubject(long subjectId, Collection<Long> exclusions, int minAge, int maxAge, int queryType);
    List<Course> queryBySubject(long subjectId, int start, int count, Collection<Long> exclusions, int minAge, int maxAge, int sortTypeId, int queryType);

    List<Course> queryAllBySubject(long subjectId);
    Map<Long, List<Course>> queryAllBySubjects(Collection<Long> subjectIds);

    List<CourseSku> querySkus(long courseId, String start, String end);
    CourseSku getSku(long skuId);
    CourseSku getBookedSku(long userId, long bookingId);

    boolean lockSku(long skuId);
    boolean unlockSku(long skuId);

    Map<Long, Date> queryStartTimesByPackages(Collection<Long> packageIds);

    BookedCourse getBookedCourse(long bookingId);

    long listFinishedCount();
    List<Course> listFinished(int start, int count);
    long listFinishedCount(long userId);
    List<Course> listFinished(long userId, int start, int count);

    long queryNotFinishedCountByUser(long userId);
    List<BookedCourse> queryNotFinishedByUser(long userId, int start, int count);
    long queryFinishedCountByUser(long userId);
    List<BookedCourse> queryFinishedByUser(long userId, int start, int count);

    List<TeacherCourse> queryOngoingByTeacher(long userId);
    long queryNotFinishedCountByTeacher(long userId);
    List<TeacherCourse> queryNotFinishedByTeacher(long userId, int start, int count);
    long queryFinishedCountByTeacher(long userId);
    List<TeacherCourse> queryFinishedByTeacher(long userId, int start, int count);

    Map<Long, Integer> queryBookedCourseCounts(Collection<Long> orderIds);
    Map<Long, Integer> queryFinishedCourseCounts(Collection<Long> orderIds);

    List<Long> queryBookedCourseIds(long packageId);

    boolean booked(long packageId, long courseId);
    long booking(long userId, long childId, long orderId, long packageId, CourseSku sku);
    void increaseJoined(long courseId, int joinCount);
    boolean cancel(long userId, long bookingId);
    void decreaseJoined(long courseId, int joinCount);

    List<Long> queryBookedPackageIds(Collection<Long> userIds, long courseId, long courseSkuId);
    void batchCancel(Collection<Long> userIds, long courseId, long courseSkuId);
    Map<Long,Long> queryBookedPackageUsers(Collection<Long> userIds, long courseId, long courseSkuId);

    CourseDetail getDetail(long courseId);
    int getInstitutionId(long courseId);

    long querySubjectId(long courseId);

    Map<Long, String> queryTips(Collection<Long> courseIds);

    boolean matched(long subjectId, long courseId);
    boolean joined(long userId, long courseId);
    boolean finished(long userId, long bookingId, long courseId);

    Map<Long, Long> queryCheckInCounts(Collection<Long> courseSkuIds);
    Map<Long, Long> queryCommentedChildrenCount(Collection<Long> courseSkuIds);

    boolean checkin(long userId, long packageId, long courseId, long courseSkuId);

    List<Student.Parent> queryParentWithoutChild(long courseId, long courseSkuId);
    List<Student> queryAllStudents(long courseId, long courseSkuId);
    List<Student> queryCheckInStudents(long courseId, long courseSkuId);
}
