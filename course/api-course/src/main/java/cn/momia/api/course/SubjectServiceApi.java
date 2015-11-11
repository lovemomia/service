package cn.momia.api.course;

import cn.momia.api.course.dto.FavoriteDto;
import cn.momia.api.course.dto.OrderPackageDto;
import cn.momia.api.course.dto.OrderDto;
import cn.momia.api.course.dto.SubjectDto;
import cn.momia.api.course.dto.SubjectSkuDto;
import cn.momia.api.course.dto.UserCouponDto;
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
    public PagedList<SubjectDto> listTrial(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("subject/trial"), builder.build());

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

    public boolean deleteOrder(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("subject/order"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean refundOrder(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("subject/order/refund"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public PagedList<OrderPackageDto> listBookableOrders(String utoken, long orderId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("subject/order/bookable"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), OrderPackageDto.class);
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

    public PagedList<UserCouponDto> listUserCoupons(String utoken, int status, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("subject/coupon/list"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), UserCouponDto.class);
    }

    public boolean favor(long userId, long subjectId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("subject", subjectId, "favor"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean unfavor(long userId, long subjectId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("subject", subjectId, "unfavor"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public PagedList<FavoriteDto> listFavorites(long userId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uid", userId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("subject/favorite"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), FavoriteDto.class);
    }
}
