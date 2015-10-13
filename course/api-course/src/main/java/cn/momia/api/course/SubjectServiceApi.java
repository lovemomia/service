package cn.momia.api.course;

import cn.momia.api.course.dto.OrderDto;
import cn.momia.api.course.dto.SubjectDto;
import cn.momia.api.course.dto.SubjectSkuDto;
import cn.momia.common.api.AbstractServiceApi;
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
}
