package cn.momia.api.course;

import cn.momia.api.course.dto.SubjectOrder;
import cn.momia.api.course.dto.SubjectPackage;
import cn.momia.common.api.HttpServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpUriRequest;

public class OrderServiceApi extends HttpServiceApi {
    public SubjectOrder placeOrder(JSONObject orderJson) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/order"), orderJson.toString());
        return executeReturnObject(request, SubjectOrder.class);
    }

    public boolean deleteOrder(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/order"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean refundOrder(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/order/refund"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public SubjectOrder get(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/order/%d", orderId), builder.build());

        return executeReturnObject(request, SubjectOrder.class);
    }

    public PagedList<SubjectPackage> listBookable(String utoken, long orderId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/order/bookable"), builder.build());

        return executeReturnPagedList(request, SubjectPackage.class);
    }

    public PagedList<SubjectOrder> listOrders(String utoken, int status, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/order/list"), builder.build());

        return executeReturnPagedList(request, SubjectOrder.class);
    }
}
