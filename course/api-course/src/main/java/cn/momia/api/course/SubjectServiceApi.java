package cn.momia.api.course;

import cn.momia.api.course.dto.OrderDto;
import cn.momia.api.course.dto.SubjectDto;
import cn.momia.api.course.dto.SubjectSkuDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class SubjectServiceApi extends ServiceApi {
    public PagedList<SubjectDto> listFree(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("subject/free"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), SubjectDto.class);
    }

    public SubjectDto get(long subjectId) {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("subject", subjectId));
        return JSON.toJavaObject((JSON) executeRequest(request), SubjectDto.class);
    }

    public List<SubjectSkuDto> querySkus(long subjectId) {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("subject", subjectId, "sku"));
        return CastUtil.toList((JSONArray) executeRequest(request), SubjectSkuDto.class);
    }

    public OrderDto placeOrder(JSONObject orderJson) {
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("subject/order"), orderJson.toString());
        return JSON.toJavaObject((JSON) executeRequest(request), OrderDto.class);
    }

    public PagedList<OrderDto> listOrders(String utoken, int status, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("subject/order/list"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), OrderDto.class);
    }

    public PagedList<OrderDto> listBookableOrders(String utoken, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("subject/order/bookable"), builder.build());

        return CastUtil.toPagedList((JSONObject) executeRequest(request), OrderDto.class);
    }
}
