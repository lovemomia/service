package cn.momia.api.course;

import cn.momia.api.course.dto.OrderDto;
import cn.momia.api.course.dto.PaymentDto;
import cn.momia.api.course.dto.SubjectDto;
import cn.momia.api.course.dto.SubjectSkuDto;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class SubjectServiceApi extends AbstractServiceApi {
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

    public List<SubjectSkuDto> listSkus(long subjectId) {
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("subject", subjectId, "sku"));
        return CastUtil.toList((JSONArray) executeRequest(request), SubjectSkuDto.class);
    }

    public OrderDto placeOrder(JSONObject orderJson) {
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("subject/order"), orderJson.toString());
        return JSON.toJavaObject((JSON) executeRequest(request), OrderDto.class);
    }

    public Object prepayAlipay(String utoken, long orderId, String type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("type", type);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("subject/payment/prepay/alipay"), builder.build());

        return executeRequest(request);
    }

    public Object prepayWeixin(String utoken, long orderId, String type, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("type", type);
        if (!StringUtils.isBlank(code)) builder.add("code", code);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("subject/payment/prepay/weixin"), builder.build());

        return executeRequest(request);
    }

    public boolean callbackAlipay(Map<String, String> params) {
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("subject/payment/callback/alipay"), params);
        return "OK".equalsIgnoreCase((String) executeRequest(request));
    }

    public boolean callbackWeixin(Map<String, String> params) {
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("subject/payment/callback/weixin"), params);
        return "OK".equalsIgnoreCase((String) executeRequest(request));
    }

    public PaymentDto checkPayment(String utoken, long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("subject/payment/check"), builder.build());

        return JSON.toJavaObject((JSON) executeRequest(request), PaymentDto.class);
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
