package cn.momia.api.course;

import cn.momia.api.course.dto.SubjectSku;
import cn.momia.api.course.dto.UserCourseComment;
import cn.momia.api.course.dto.Favorite;
import cn.momia.api.course.dto.SubjectDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class SubjectServiceApi extends ServiceApi {
    public PagedList<SubjectDto> listTrial(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/trial"), builder.build());

        return executeReturnPagedList(request, SubjectDto.class);
    }

    public SubjectDto get(long subjectId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/%d", subjectId));
        return executeReturnObject(request, SubjectDto.class);
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

    public PagedList<Favorite> listFavorites(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/favorite"), builder.build());

        return executeReturnPagedList(request, Favorite.class);
    }
}
