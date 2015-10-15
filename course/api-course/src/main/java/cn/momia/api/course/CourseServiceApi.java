package cn.momia.api.course;

import cn.momia.api.course.dto.CourseDto;
import cn.momia.api.course.dto.CourseSkuDto;
import cn.momia.api.course.dto.DatedCourseSkusDto;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class CourseServiceApi extends AbstractServiceApi {
    public CourseDto get(long courseId) {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("course", courseId));
        return JSON.toJavaObject((JSON) executeRequest(request), CourseDto.class);
    }

    public PagedList<CourseDto> query(long subjectId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("suid", subjectId)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("course/query"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), CourseDto.class);
    }

    public PagedList<CourseDto> query(long subjectId, int minAge, int maxAge, int sortTypeId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("suid", subjectId)
                .add("min", minAge)
                .add("max", maxAge)
                .add("sort", sortTypeId)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("course/query"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), CourseDto.class);
    }

    public List<DatedCourseSkusDto> listWeekSkus(long courseId) {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("course", courseId, "sku/week"));
        return CastUtil.toList((JSONArray) executeRequest(request), DatedCourseSkusDto.class);
    }

    public List<DatedCourseSkusDto> listMonthSkus(long courseId, int month) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("month", month);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("course", courseId, "sku/month"), builder.build());

        return CastUtil.toList((JSONArray) executeRequest(request), DatedCourseSkusDto.class);
    }

    public List<CourseSkuDto> listMoreSkus(long courseId, String date, String excludes) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("date", date)
                .add("excludes", excludes);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("course", courseId, "sku/more"), builder.build());

        return CastUtil.toList((JSONArray) executeRequest(request), CourseSkuDto.class);
    }

    public PagedList<CourseDto> queryNotFinishedByUser(long userId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("course/notfinished"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), CourseDto.class);
    }

    public PagedList<CourseDto> queryFinishedByUser(long userId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("course/finished"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), CourseDto.class);
    }

    public boolean isFavored(long userId, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("course", courseId, "favored"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean favor(long userId, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("course", courseId, "favor"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean unfavor(long userId, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("course", courseId, "unfavor"), builder.build());

        return (Boolean) executeRequest(request);
    }
}
