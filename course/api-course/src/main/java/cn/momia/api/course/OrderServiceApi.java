package cn.momia.api.course;

import cn.momia.api.course.dto.subject.SubjectOrder;
import cn.momia.api.course.dto.subject.SubjectPackage;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class OrderServiceApi extends HttpServiceApi {
    public SubjectOrder placeOrder(JSONObject orderJson) {
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/order"), orderJson.toString()), SubjectOrder.class);
    }

    public boolean deleteOrder(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        return executeReturnObject(MomiaHttpRequestBuilder.DELETE(url("/order"), builder.build()), Boolean.class);
    }

    public boolean refundOrder(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/order/refund"), builder.build()), Boolean.class);
    }

    public SubjectOrder get(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/order/%d", orderId), builder.build()), SubjectOrder.class);
    }

    public PagedList<SubjectPackage> listBookable(String utoken, long orderId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/order/bookable"), builder.build()), SubjectPackage.class);
    }

    public PagedList<SubjectOrder> listOrders(String utoken, int status, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("start", start)
                .add("count", count);
        return executeReturnPagedList(MomiaHttpRequestBuilder.GET(url("/order/list"), builder.build()), SubjectOrder.class);
    }

    public long bookablePackageId(String utoken, long courseId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/order/bookable/package", courseId), builder.build()), Number.class).longValue();
    }

    public boolean sendGift(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/order/%d/gift/send", orderId), builder.build()), Boolean.class);
    }

    public int giftStatus(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/order/%d/gift/status", orderId), builder.build()), Integer.class);
    }

    public boolean receiveGift(String utoken, long orderId, long expired, String giftsign) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("expired", expired)
                .add("giftsign", giftsign);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/order/%d/gift/receive", orderId), builder.build()), Boolean.class);
    }

    public boolean extendPackageTime(long packageId, int time) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("pid", packageId)
                .add("time", time);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/order/package/time/extend"), builder.build()), Boolean.class);
    }

    public List<Long> queryBookableUserIds() {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/order/bookable/user")), Long.class);
    }

    public List<Long> queryUserIdsOfPackagesToExpired(int days) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("days", days);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/order/package/expired/user"), builder.build()), Long.class);
    }
}
