package cn.momia.api.course;

import cn.momia.api.course.dto.OrderSkuDto;
import cn.momia.api.course.dto.OrderDto;
import cn.momia.api.course.dto.SubjectDto;
import cn.momia.api.course.dto.SubjectSkuDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class SubjectServiceApi extends ServiceApi {
    public PagedList<SubjectDto> listFree(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("subject/free"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), SubjectDto.class);
    }

    public SubjectDto get(long subjectId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("subject", subjectId));
        return CastUtil.toObject((JSON) executeRequest(request), SubjectDto.class);
    }

    public List<SubjectSkuDto> querySkus(long subjectId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("subject", subjectId, "sku"));
        return CastUtil.toList((JSON) executeRequest(request), SubjectSkuDto.class);
    }

    public OrderDto placeOrder(JSONObject orderJson) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("subject/order"), orderJson.toString());
        return CastUtil.toObject((JSON) executeRequest(request), OrderDto.class);
    }

    public PagedList<OrderDto> listOrders(String utoken, int status, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("subject/order/list"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), OrderDto.class);
    }

    public PagedList<OrderSkuDto> listBookableOrders(String utoken, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("subject/order/bookable"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), OrderSkuDto.class);
    }
}
