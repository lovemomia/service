package cn.momia.api.course;

import cn.momia.api.course.dto.CourseCommentDto;
import cn.momia.api.course.dto.FavoriteDto;
import cn.momia.api.course.dto.SubjectDto;
import cn.momia.api.course.dto.SubjectSkuDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class SubjectServiceApi extends ServiceApi {
    public PagedList<SubjectDto> listTrial(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/trial"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), SubjectDto.class);
    }

    public SubjectDto get(long subjectId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/%d", subjectId));
        return CastUtil.toObject((JSON) executeRequest(request), SubjectDto.class);
    }

    public List<SubjectSkuDto> querySkus(long subjectId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/%d/sku", subjectId));
        return CastUtil.toList((JSON) executeRequest(request), SubjectSkuDto.class);
    }

    public PagedList<CourseCommentDto> queryCommentsBySubject(long subjectId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/%d/comment", subjectId), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), CourseCommentDto.class);
    }

    public boolean favor(long userId, long subjectId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/subject/%d/favor", subjectId), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean unfavor(long userId, long subjectId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/subject/%d/unfavor", subjectId), builder.build());

        return (Boolean) executeRequest(request);
    }

    public PagedList<FavoriteDto> listFavorites(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/subject/favorite"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), FavoriteDto.class);
    }
}
