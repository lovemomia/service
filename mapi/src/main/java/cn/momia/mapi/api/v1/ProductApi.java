package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/product")
public class ProductApi extends AbstractApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductApi.class);

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getProducts(@RequestParam int start, @RequestParam int count, @RequestParam(value = "query") String queryJson) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("start", String.valueOf(start));
        params.put("count", String.valueOf(count));
        params.put("query", queryJson);
        MomiaHttpRequest request = new MomiaHttpGetRequest("products", true, baseServiceUrl("product"), params);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getProduct(@PathVariable long id) {
        List<MomiaHttpRequest> requests = buildProductRequests(id);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, JSONObject>() {
            @Override
            public JSONObject apply(MomiaHttpResponseCollector collector) {
                JSONObject productObject = new JSONObject();
                productObject.put("product", collector.getResponse("product"));
                productObject.put("skus", collector.getResponse("skus"));
                productObject.put("place", collector.getResponse("place"));
                productObject.put("server", collector.getResponse("server"));

                JSONObject comments = collector.getResponse("comments");
                if (comments != null) productObject.put("comments", comments);

                JSONObject customers = collector.getResponse("customers");
                if (customers != null) productObject.put("customers", customers);

                return productObject;
            }
        });
    }

    private List<MomiaHttpRequest> buildProductRequests(long productId) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        requests.add(buildProductInfoRequest(productId));
        requests.add(buildProductSkusRequest(productId));
        requests.add(buildProductPlaceRequest(productId));
        requests.add(buildProductCommentsRequest(productId));
        requests.add(buildProductServerRequest(productId));
        requests.add(buildProductCustomersRequest(productId));

        return requests;
    }

    private MomiaHttpRequest buildProductInfoRequest(long productId) {
        return new MomiaHttpGetRequest("product", true, baseServiceUrl("product", productId), null);
    }

    private MomiaHttpRequest buildProductSkusRequest(long productId) {
        return new MomiaHttpGetRequest("skus", true, baseServiceUrl("product", productId, "sku"), null);
    }

    private MomiaHttpRequest buildProductPlaceRequest(long productId) {
        return new MomiaHttpGetRequest("place", true, baseServiceUrl("product", productId, "place"), null);
    }

    private MomiaHttpRequest buildProductCommentsRequest(long productId) {
        return new MomiaHttpGetRequest("comments", false, baseServiceUrl("product", productId, "comment"), null);
    }

    private MomiaHttpRequest buildProductServerRequest(long productId) {
        return new MomiaHttpGetRequest("server", true, baseServiceUrl("product", productId, "server"), null);
    }

    private MomiaHttpRequest buildProductCustomersRequest(long productId) {
        return new MomiaHttpGetRequest("customers", false, baseServiceUrl("product", productId, "customer"), null);
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(@PathVariable long id) {
        return executeRequest(buildProductSkusRequest(id));
    }
}
