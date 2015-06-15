package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
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
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage home(@RequestParam(value = "pageindex") final int pageIndex) {
        List<MomiaHttpRequest> requests = buildHomeRequests(pageIndex);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, JSONObject>() {
            @Override
            public JSONObject apply(MomiaHttpResponseCollector collector) {
                JSONObject homeData = new JSONObject();
                if (pageIndex == 0) homeData.put("banners", collector.getResponse("banners"));
                homeData.put("products", collector.getResponse("products"));

                return homeData;
            }
        });
    }

    private List<MomiaHttpRequest> buildHomeRequests(int pageIndex) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        if (pageIndex == 0) requests.add(buildBannersRequest());
        requests.add(buildProductsRequest(pageIndex));

        return requests;
    }

    private MomiaHttpRequest buildBannersRequest() {
        return new MomiaHttpGetRequest("banners", true, baseServiceUrl("banner"));
    }

    private MomiaHttpRequest buildProductsRequest(int pageIndex) {
        int pageSize = conf.getInt("Home.PageSize");
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", String.valueOf(pageIndex * pageSize))
                .add("count", String.valueOf(pageSize));

        return new MomiaHttpGetRequest("products", true, baseServiceUrl("product"), builder.build());
    }
}
