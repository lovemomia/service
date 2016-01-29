package cn.momia.api.course;

import cn.momia.api.course.dto.subject.SubjectOrder;
import cn.momia.api.course.dto.subject.SubjectPackage;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

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

    public boolean sendGift(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/order/%d/gift/send", orderId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean extendPackageTime(long packageId, int time) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("pid", packageId)
                .add("time", time);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/order/package/time/extend"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public List<Long> queryBookableUserIds() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/order/bookable/user"));
        return executeReturnList(request, Long.class);
    }

    public List<Long> queryUserIdsOfPackagesToExpired(int days) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("days", days);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/order/package/expired/user"), builder.build());

        return executeReturnList(request, Long.class);
    }
}
