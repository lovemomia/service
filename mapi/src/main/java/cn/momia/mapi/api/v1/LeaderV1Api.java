package cn.momia.mapi.api.v1;

import cn.momia.common.service.config.Configuration;
import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/leader")
public class LeaderV1Api extends AbstractV1Api {
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public ResponseMessage applyLeader(@RequestParam String utoken, @RequestParam(value = "pid") long productId, @RequestParam(value = "sid") long skuId) {
        if (StringUtils.isBlank(utoken) || productId <= 0 || skuId <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("pid", productId)
                .add("sid", skuId);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("leader/apply"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseMessage addLeaderInfo(@RequestParam String utoken, @RequestParam String leader) {
        if (StringUtils.isBlank(utoken) || StringUtils.isBlank(leader)) return ResponseMessage.BAD_REQUEST;

        JSONObject leaderJson = JSON.parseObject(leader);
        leaderJson.put("userId", getUserId(utoken));
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("leader"), leaderJson.toString());

        return executeRequest(request);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseMessage updateLeaderInfo(@RequestParam String utoken, @RequestParam String leader) {
        if (StringUtils.isBlank(utoken) || StringUtils.isBlank(leader)) return ResponseMessage.BAD_REQUEST;

        JSONObject leaderJson = JSON.parseObject(leader);
        leaderJson.put("userId", getUserId(utoken));
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("leader"), leaderJson.toString());

        return executeRequest(request);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseMessage deleteLeaderInfo(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("leader"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/product/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkusNeedLeader(@RequestParam(value = "pid") long productId) {
        if (productId <= 0) return ResponseMessage.BAD_REQUEST;

        List<MomiaHttpRequest> requests = buildRequests(productId);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Object>() {
            @Override
            public Object apply(MomiaHttpResponseCollector collector) {
                JSONObject productSkusJson = new JSONObject();
                productSkusJson.put("product", collector.getResponse("product"));
                productSkusJson.put("skus", collector.getResponse("skus"));

                return productSkusJson;
            }
        });
    }

    private List<MomiaHttpRequest> buildRequests(long productId) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        requests.add(buildProductRequest(productId));
        requests.add(buildNeedLeaderSkusRequest(productId));

        return requests;
    }

    private MomiaHttpRequest buildProductRequest(long productId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("full", false);

        return MomiaHttpRequest.GET("product", true, url("product", productId), builder.build());
    }

    private MomiaHttpRequest buildNeedLeaderSkusRequest(long productId) {
        return MomiaHttpRequest.GET("skus", true, url("product", productId, "sku/leader"));
    }

    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public ResponseMessage getLedProducts(@RequestParam String utoken, @RequestParam int start) {
        if (StringUtils.isBlank(utoken) || start < 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("start", start)
                .add("count", Configuration.getInt("Leader.Product.PageSize"));
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("leader/product"), builder.build());

        return executeRequest(request, pagedProductsFunc);
    }
}
