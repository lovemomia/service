package cn.momia.api.course;

import cn.momia.api.course.dto.subject.Subject;
import cn.momia.api.course.dto.subject.SubjectSku;
import cn.momia.api.course.dto.comment.UserCourseComment;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;

import java.util.List;

public class SubjectServiceApi extends HttpServiceApi {
    public PagedList<Subject> listTrial(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/subject/trial"), builder.build()), Subject.class);
    }

    public Subject get(long subjectId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/subject/%d", subjectId)), Subject.class);
    }

    public List<Subject> list(int cityId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("city", cityId);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/subject/list"), builder.build()), Subject.class);
    }

    public List<SubjectSku> querySkus(long subjectId) {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/subject/%d/sku", subjectId)), SubjectSku.class);
    }

    public PagedList<UserCourseComment> queryCommentsBySubject(long subjectId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/subject/%d/comment", subjectId), builder.build()), UserCourseComment.class);
    }

    public List<UserCourseComment> queryRecommendedCommentsBySubject(long subjectId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/subject/%d/comment/recommend", subjectId), builder.build()), UserCourseComment.class);
    }
}
