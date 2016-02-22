package cn.momia.api.course;

import cn.momia.api.course.dto.subject.Subject;
import cn.momia.api.course.dto.subject.SubjectSku;
import cn.momia.api.course.dto.comment.UserCourseComment;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class SubjectServiceApi extends HttpServiceApi {
    public PagedList<Subject> listTrial(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/trial"), builder.build());

        return executeReturnPagedList(request, Subject.class);
    }

    public Subject get(long subjectId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/%d", subjectId));
        return executeReturnObject(request, Subject.class);
    }

    public List<Subject> list(int cityId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("city", cityId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/list"), builder.build());

        return executeReturnList(request, Subject.class);
    }

    public List<SubjectSku> querySkus(long subjectId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/%d/sku", subjectId));
        return executeReturnList(request, SubjectSku.class);
    }

    public PagedList<UserCourseComment> queryCommentsBySubject(long subjectId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/%d/comment", subjectId), builder.build());

        return executeReturnPagedList(request, UserCourseComment.class);
    }

    public List<UserCourseComment> queryRecommendedCommentsBySubject(long subjectId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/%d/comment/recommend", subjectId), builder.build());

        return executeReturnList(request, UserCourseComment.class);
    }

    public boolean favor(long userId, long subjectId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/subject/%d/favor", subjectId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean unfavor(long userId, long subjectId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/subject/%d/unfavor", subjectId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
