package cn.momia.api.course;

import cn.momia.api.course.dto.BookedCourseDto;
import cn.momia.api.course.dto.CourseCommentDto;
import cn.momia.api.course.dto.CourseDetailDto;
import cn.momia.api.course.dto.CourseDto;
import cn.momia.api.course.dto.DatedCourseSkusDto;
import cn.momia.api.course.dto.FavoriteDto;
import cn.momia.api.course.dto.InstitutionDto;
import cn.momia.api.course.dto.TeacherDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class CourseServiceApi extends ServiceApi {
    public PagedList<CourseDto> listRecommend(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/recommend"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), CourseDto.class);
    }

    public PagedList<CourseDto> listTrial(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/trial"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), CourseDto.class);
    }

    public CourseDto get(long courseId) {
        return get(courseId, CourseDto.Type.FULL);
    }

    public CourseDto get(long courseId, int type) {
        return get(courseId, "", type);
    }

    public CourseDto get(long courseId, String pos) {
        return get(courseId, pos, CourseDto.Type.FULL);
    }

    public CourseDto get(long courseId, String pos, int type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("pos", pos)
                .add("type", type);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/%d", courseId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), CourseDto.class);
    }

    public boolean isBuyable(long courseId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/%d/buyable", courseId));
        return (Boolean) executeRequest(request);
    }

    public PagedList<CourseDto> listFinished(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/finished/list"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), CourseDto.class);
    }

    public PagedList<CourseDto> query(long subjectId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("suid", subjectId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/query"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), CourseDto.class);
    }

    public PagedList<CourseDto> query(long subjectId, long packageId, int minAge, int maxAge, int sortTypeId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("suid", subjectId)
                .add("pid", packageId)
                .add("min", minAge)
                .add("max", maxAge)
                .add("sort", sortTypeId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/query"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), CourseDto.class);
    }

    public CourseDetailDto detail(long courseId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/%d/detail", courseId));
        return CastUtil.toObject((JSON) executeRequest(request), CourseDetailDto.class);
    }

    public PagedList<String> book(long courseId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/%d/book", courseId), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), String.class);
    }

    public PagedList<TeacherDto> teacher(long courseId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/%d/teacher", courseId), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), TeacherDto.class);
    }

    public InstitutionDto institution(long courseId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/%d/institution", courseId));
        return CastUtil.toObject((JSON) executeRequest(request), InstitutionDto.class);
    }

    public List<DatedCourseSkusDto> listWeekSkus(long courseId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/%d/sku/week", courseId));
        return CastUtil.toList((JSON) executeRequest(request), DatedCourseSkusDto.class);
    }

    public List<DatedCourseSkusDto> listMonthSkus(long courseId, int month) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("month", month);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/%d/sku/month", courseId), builder.build());

        return CastUtil.toList((JSON) executeRequest(request), DatedCourseSkusDto.class);
    }

    public PagedList<BookedCourseDto> queryNotFinishedByUser(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/notfinished"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), BookedCourseDto.class);
    }

    public PagedList<BookedCourseDto> queryFinishedByUser(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/finished"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), BookedCourseDto.class);
    }

    public boolean joined(long userId, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/%d/joined", courseId), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean booking(String utoken, long packageId, long skuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("pid", packageId)
                .add("sid", skuId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/course/booking"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean cancel(String utoken, long bookingId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("bid", bookingId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/course/cancel"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean comment(JSONObject commentJson) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/course/comment"), commentJson.toString());
        return (Boolean) executeRequest(request);
    }

    public PagedList<CourseCommentDto> queryCommentsByCourse(long courseId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/%d/comment", courseId), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), CourseCommentDto.class);
    }

    public boolean isFavored(long userId, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/%d/favored", courseId), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean favor(long userId, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/course/%d/favor", courseId), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean unfavor(long userId, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/course/%d/unfavor", courseId), builder.build());

        return (Boolean) executeRequest(request);
    }

    public PagedList<FavoriteDto> listFavorites(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/course/favorite"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), FavoriteDto.class);
    }
}
