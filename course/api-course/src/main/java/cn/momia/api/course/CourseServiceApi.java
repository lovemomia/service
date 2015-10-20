package cn.momia.api.course;

import cn.momia.api.course.dto.CourseDetailDto;
import cn.momia.api.course.dto.CourseDto;
import cn.momia.api.course.dto.DatedCourseSkusDto;
import cn.momia.api.course.dto.InstitutionDto;
import cn.momia.api.course.dto.TeacherDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class CourseServiceApi extends ServiceApi {
    public CourseDto get(long courseId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course", courseId));
        return CastUtil.toObject((JSON) executeRequest(request), CourseDto.class);
    }

    public PagedList<CourseDto> query(long subjectId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("suid", subjectId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course/query"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), CourseDto.class);
    }

    public PagedList<CourseDto> query(long subjectId, int minAge, int maxAge, int sortTypeId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("suid", subjectId)
                .add("min", minAge)
                .add("max", maxAge)
                .add("sort", sortTypeId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course/query"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), CourseDto.class);
    }

    public CourseDetailDto detail(long courseId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course", courseId, "detail"));
        return CastUtil.toObject((JSON) executeRequest(request), CourseDetailDto.class);
    }

    public PagedList<String> book(long courseId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course", courseId, "book"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), String.class);
    }

    public PagedList<TeacherDto> queryTeachers(long courseId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course", courseId, "teacher"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), TeacherDto.class);
    }

    public InstitutionDto institution(long courseId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course", courseId, "institution"));
        return CastUtil.toObject((JSON) executeRequest(request), InstitutionDto.class);
    }

    public List<DatedCourseSkusDto> listWeekSkus(long courseId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course", courseId, "sku/week"));
        return CastUtil.toList((JSON) executeRequest(request), DatedCourseSkusDto.class);
    }

    public List<DatedCourseSkusDto> listMonthSkus(long courseId, int month) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("month", month);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course", courseId, "sku/month"), builder.build());

        return CastUtil.toList((JSON) executeRequest(request), DatedCourseSkusDto.class);
    }

    public PagedList<CourseDto> queryNotFinishedByUser(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course/notfinished"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), CourseDto.class);
    }

    public PagedList<CourseDto> queryFinishedByUser(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course/finished"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), CourseDto.class);
    }

    public boolean booking(String utoken, long packageId, long skuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("pkgid", packageId)
                .add("sid", skuId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("course/booking"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean isFavored(long userId, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("course", courseId, "favored"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean favor(long userId, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("course", courseId, "favor"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean unfavor(long userId, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("course", courseId, "unfavor"), builder.build());

        return (Boolean) executeRequest(request);
    }
}
