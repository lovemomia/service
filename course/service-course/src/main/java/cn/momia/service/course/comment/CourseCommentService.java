package cn.momia.service.course.comment;

import java.util.Collection;
import java.util.List;

public interface CourseCommentService {
    boolean isCommented(long userId, long bookingId);
    boolean comment(CourseComment comment);

    long queryCommentCountByCourse(long courseId);
    List<CourseComment> queryCommentsByCourse(long courseId, int start, int count);
    long queryCommentCountBySubject(long subjectId);
    List<CourseComment> queryCommentsBySubject(long subjectId, int start, int count);
    List<CourseComment> queryRecommendedCommentsBySubject(long subjectId, int start, int count);
    long queryCommentCountByUser(long userId);
    List<CourseComment> queryCommentsByUser(long userId, int start, int count);

    List<CourseComment> queryComments(long userId, Collection<Long> courseIds);

    List<Long> queryCommentedBookingIds(long userId, Collection<Long> courseIds);

    List<String> queryLatestImgs(long userId);
}
