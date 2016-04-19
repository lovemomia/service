package cn.momia.api.course;

import cn.momia.api.course.dto.course.BookedCourse;
import cn.momia.api.course.dto.course.Course;
import cn.momia.api.course.dto.material.CourseMaterial;
import cn.momia.api.course.dto.course.CourseSku;
import cn.momia.api.course.dto.course.Student;
import cn.momia.api.course.dto.course.TeacherCourse;
import cn.momia.api.course.dto.comment.TimelineUnit;
import cn.momia.api.course.dto.comment.UserCourseComment;
import cn.momia.api.course.dto.course.CourseDetail;
import cn.momia.api.course.dto.course.DatedCourseSkus;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseServiceApi extends HttpServiceApi {
    public PagedList<Course> listRecommend(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/recommend"), builder.build()), Course.class);
    }

    public PagedList<Course> listTrial(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/trial"), builder.build()), Course.class);
    }

    public Course get(long courseId) {
        return get(courseId, Course.ShowType.FULL);
    }

    public Course get(long courseId, int type) {
        return get(courseId, "", type);
    }

    public Course get(long courseId, String pos) {
        return get(courseId, pos, Course.ShowType.FULL);
    }

    public Course get(long courseId, String pos, int type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("pos", pos)
                .add("type", type);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/course/%d", courseId), builder.build()), Course.class);
    }

    public List<Course> list(Collection<Long> courseIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("coids", StringUtils.join(courseIds, ","));
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/list"), builder.build()), Course.class);
    }

    public PagedList<Course> listFinished(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/finished/list"), builder.build()), Course.class);
    }

    public CourseSku getBookedSku(long userId, long bookingId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("bid", bookingId);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/course/booked/sku"), builder.build()), CourseSku.class);
    }

    public List<Course> listRecentCoursesBySubject(long subjectId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("suid", subjectId);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/list/subject/recent"), builder.build()), Course.class);
    }

    public List<Course> listBySubject(long subjectId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("suid", subjectId);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/list/subject"), builder.build()), Course.class);
    }

    public PagedList<Course> query(long subjectId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("suid", subjectId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/query"), builder.build()), Course.class);
    }

    public PagedList<Course> query(long subjectId, long packageId, int minAge, int maxAge, int sortTypeId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("suid", subjectId)
                .add("pid", packageId)
                .add("min", minAge)
                .add("max", maxAge)
                .add("sort", sortTypeId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/query"), builder.build()), Course.class);
    }

    public CourseDetail detail(long courseId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/course/%d/detail", courseId)), CourseDetail.class);
    }

    public PagedList<String> book(long courseId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/%d/book", courseId), builder.build()), String.class);
    }

    public PagedList<Integer> teacherIds(long courseId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/%d/teacher", courseId), builder.build()), Integer.class);
    }

    public Map<Long, String> queryTips(Set<Long> courseIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("coids", StringUtils.join(courseIds, ","));
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/course/tips"), builder.build()), Map.class);
    }

    public CourseSku getSku(long courseId, long skuId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/course/%d/sku/%d", courseId, skuId)), CourseSku.class);
    }

    public List<CourseSku> listSkus(Collection<Long> courseSkuIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("sids", StringUtils.join(courseSkuIds, ","));
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/sku/list"), builder.build()), CourseSku.class);
    }

    public List<DatedCourseSkus> listSkus(long courseId) {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/%d/sku", courseId)), DatedCourseSkus.class);
    }

    public List<DatedCourseSkus> listWeekSkus(long courseId) {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/%d/sku/week", courseId)), DatedCourseSkus.class);
    }

    public List<DatedCourseSkus> listMonthSkus(long courseId, int month) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("month", month);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/%d/sku/month", courseId), builder.build()), DatedCourseSkus.class);
    }

    public PagedList<BookedCourse> queryNotFinishedByUser(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/notfinished"), builder.build()), BookedCourse.class);
    }

    public PagedList<BookedCourse> queryFinishedByUser(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/finished"), builder.build()), BookedCourse.class);
    }

    public TeacherCourse ongoingTeacherCourse(long userId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/course/teacher/ongoing"), builder.build()), TeacherCourse.class);
    }

    public PagedList<TeacherCourse> queryNotFinishedByTeacher(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/teacher/notfinished"), builder.build()), TeacherCourse.class);
    }

    public PagedList<TeacherCourse> queryFinishedByTeacher(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/teacher/finished"), builder.build()), TeacherCourse.class);
    }

    public boolean joined(long userId, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/course/%d/joined", courseId), builder.build()), Boolean.class);
    }

    public BookedCourse booking(String utoken, long childId, long packageId, long skuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("cid", childId)
                .add("pid", packageId)
                .add("sid", skuId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/course/booking"), builder.build()), BookedCourse.class);
    }

    public List<Long> batchBooking(Collection<Long> userIds, long courseId, long skuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uids", StringUtils.join(userIds, ","))
                .add("coid", courseId)
                .add("sid", skuId);
        return executeReturnList(MomiaHttpRequestBuilder.POST(url("/course/booking/batch"), builder.build()), Long.class);
    }

    public BookedCourse cancel(String utoken, long bookingId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("bid", bookingId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/course/cancel"), builder.build()), BookedCourse.class);
    }

    public Map<Long, Long> batchCancel(Collection<Long> userIds, long courseId, long skuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uids", StringUtils.join(userIds, ","))
                .add("coid", courseId)
                .add("sid", skuId);
        Map<String, Object> data = executeReturnObject(MomiaHttpRequestBuilder.POST(url("/course/cancel/batch"), builder.build()), Map.class);

        Map<Long, Long> result = new HashMap<Long, Long>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result.put(Long.valueOf(entry.getKey()), ((Number) (entry.getValue())).longValue());
        }

        return result;
    }

    public Map<Long, Long> skuCancel(long courseId, long skuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("coid", courseId)
                .add("sid", skuId);
        Map<String, Object> data = executeReturnObject(MomiaHttpRequestBuilder.POST(url("/course/sku/cancel"), builder.build()), Map.class);

        Map<Long, Long> result = new HashMap<Long, Long>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result.put(Long.valueOf(entry.getKey()), ((Number) (entry.getValue())).longValue());
        }

        return result;
    }

    public boolean comment(JSONObject commentJson) {
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/course/comment"), commentJson.toString()), Boolean.class);
    }

    public PagedList<UserCourseComment> queryCommentsByCourse(long courseId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/%d/comment", courseId), builder.build()), UserCourseComment.class);
    }

    public List<String> getLatestCommentImgs(long userId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/comment/img"), builder.build()), String.class);
    }

    public PagedList<TimelineUnit> timelineOfUser(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/timeline"), builder.build()), TimelineUnit.class);
    }

    public PagedList<TimelineUnit> commentTimelineOfUser(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/comment/timeline"), builder.build()), TimelineUnit.class);
    }

    public CourseMaterial getMaterial(String utoken, int materialId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/course/material/%d", materialId), builder.build()), CourseMaterial.class);
    }

    public PagedList<CourseMaterial> listMaterials(String utoken, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/course/material/list"), builder.build()), CourseMaterial.class);
    }

    public boolean checkin(String utoken, long userId, long packageId, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("uid", userId)
                .add("pid", packageId)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/course/checkin"), builder.build()), Boolean.class);
    }

    public List<Student> ongoingStudents(String utoken, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/ongoing/student"), builder.build()), Student.class);
    }

    public List<Student> notfinishedStudents(String utoken, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/notfinished/student"), builder.build()), Student.class);
    }

    public List<Student> finishedStudents(String utoken, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/finished/student"), builder.build()), Student.class);
    }

    public List<Long> queryUserIdsOfTodaysCourse() {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/today/user")), Long.class);
    }

    public List<String> queryHotNewCourses() {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/hot/new")), String.class);
    }

    public List<CourseSku> queryCourseSkusClosedToday() {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/sku/closed/today")), CourseSku.class);
    }

    public List<Long> queryBookedUserIds(long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("sid", courseSkuId);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/course/sku/booked/user"), builder.build()), Long.class);
    }
}
