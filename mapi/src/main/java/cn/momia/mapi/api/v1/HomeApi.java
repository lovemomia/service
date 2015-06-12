package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/home")
public class HomeApi extends AbstractApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeApi.class);

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage home(@RequestParam(value = "pageindex") int pageIndex) {
        List<MomiaHttpRequest> requests = buildHomeRequests(pageIndex);
        MomiaHttpResponseCollector collector = new MomiaHttpResponseCollector();
        List<Throwable> exceptions = new ArrayList<Throwable>();
        boolean successful = requestExecutor.execute(requests, collector, exceptions, requests.size());
        if (!successful) {
            LOGGER.error("fail to get home data, exceptions: {}", exceptions);
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to get home data");
        }

        return new ResponseMessage(buildHomeResponse(collector, pageIndex));
    }

    private List<MomiaHttpRequest> buildHomeRequests(int pageIndex) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        if (pageIndex == 0) requests.add(buildBannersRequest());
        requests.add(buildProductsRequest(pageIndex));

        return requests;
    }

    private MomiaHttpRequest buildBannersRequest() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(conf.getString("Service.Base")).append("/banner");

        return new MomiaHttpGetRequest("banners", true, urlBuilder.toString(), null);
    }

    private MomiaHttpRequest buildProductsRequest(int pageIndex) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(conf.getString("Service.Base")).append("/product");
        int pageSize = conf.getInt("Home.PageSize");
        Map<String, String> params = new HashMap<String, String>();
        params.put("start", String.valueOf(pageIndex * pageSize));
        params.put("count", String.valueOf(pageSize));

        return new MomiaHttpGetRequest("products", true, urlBuilder.toString(), params);
    }

    private JSONObject buildHomeResponse(MomiaHttpResponseCollector collector, int pageIndex) {
        JSONObject homeData = new JSONObject();
        if (pageIndex == 0) homeData.put("banners", collector.get("banners"));
        homeData.put("products", collector.get("products"));

        return homeData;
    }
}
