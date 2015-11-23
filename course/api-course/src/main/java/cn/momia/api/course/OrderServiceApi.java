package cn.momia.api.course;

import cn.momia.api.course.dto.OrderDto;
import cn.momia.api.course.dto.OrderPackageDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpUriRequest;

public class OrderServiceApi extends ServiceApi {
    public OrderDto placeOrder(JSONObject orderJson) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/order"), orderJson.toString());
        return CastUtil.toObject((JSON) executeRequest(request), OrderDto.class);
    }

    public boolean deleteOrder(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/order"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public boolean refundOrder(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/order/refund"), builder.build());

        return (Boolean) executeRequest(request);
    }

    public OrderDto get(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/order/%d", orderId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), OrderDto.class);
    }

    public PagedList<OrderPackageDto> listBookable(String utoken, long orderId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/order/bookable"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), OrderPackageDto.class);
    }

    public PagedList<OrderDto> listOrders(String utoken, int status, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/order/list"), builder.build());

        return CastUtil.toPagedList((JSON) executeRequest(request), OrderDto.class);
    }
}
