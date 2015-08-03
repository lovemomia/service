package cn.momia.mapi.api.v1;

import cn.momia.common.service.config.Configuration;
import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/leader")
public class LeaderV1Api extends AbstractV1Api {
    public static class Status {
        public static final int NOTEXIST = 0;
        public static final int PASSED = 1;
        public static final int AUDITING = 2;
        public static final int REJECTED = 3;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseMessage getLeaderStatus(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("leader/status"), builder.build());

        ResponseMessage statusResponse = executeRequest(request);
        if (!statusResponse.successful()) return ResponseMessage.FAILED("获取领队状态失败");

        JSONObject statusJson = (JSONObject) statusResponse.getData();
        int status = statusJson.getInteger("status");
        if (status == Status.PASSED) {
            ResponseMessage ledProductsResponse = getLedProducts(utoken, 0, Configuration.getInt("Leader.Product.PageSize"));
            if (!ledProductsResponse.successful()) return ResponseMessage.FAILED("获取领队状态失败");

            statusJson.put("products", ledProductsResponse.getData());
        } else if (status == Status.NOTEXIST || status == Status.AUDITING) {
            statusJson.put("desc", "");
        }

        return new ResponseMessage(statusJson);
    }

    private ResponseMessage getLedProducts(String utoken, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("leader/product"), builder.build());

        return executeRequest(request, pagedProductsFunc);
    }

    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public ResponseMessage getLedProducts(@RequestParam String utoken, @RequestParam int start) {
        if (StringUtils.isBlank(utoken) || start < 0) return ResponseMessage.BAD_REQUEST;

        return getLedProducts(utoken, start, Configuration.getInt("Leader.Product.PageSize"));
    }

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
}
