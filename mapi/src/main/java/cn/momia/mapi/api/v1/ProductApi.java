package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.response.ErrorCode;
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
import java.util.List;

@RestController
@RequestMapping("/v1/product")
public class ProductApi extends AbstractApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductApi.class);

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getProducts(@RequestParam int start, @RequestParam int count, @RequestParam String query) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count)
                .add("query", query);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("product"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getProduct(@PathVariable long id) {
        List<MomiaHttpRequest> requests = buildProductRequests(id);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, JSONObject>() {
            @Override
            public JSONObject apply(MomiaHttpResponseCollector collector) {
                JSONObject productObject = new JSONObject();

                JSONObject product = (JSONObject) collector.getResponse("product");
                productObject.put("product", product);
                productObject.put("skus", collector.getResponse("skus"));
                productObject.put("server", collector.getResponse("server"));

                Object comments = collector.getResponse("comments");
                if (comments != null) productObject.put("comments", comments);

                Object customers = collector.getResponse("customers");
                if (customers != null) productObject.put("customers", customers);

                Long placeId = product.getJSONObject("content").getLong("placeId");
                if (placeId != null) {
                    ResponseMessage placeResponse = executeRequest(new MomiaHttpGetRequest(baseServiceUrl("place", placeId)));
                    if (placeResponse.getErrno() == ErrorCode.SUCCESS) {
                        productObject.put("place", placeResponse.getData());
                    } else {
                        LOGGER.error("fail to get place of product: {}", product.getInteger("id"));
                    }
                }

                return productObject;
            }
        });
    }

    private List<MomiaHttpRequest> buildProductRequests(long productId) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        requests.add(buildProductInfoRequest(productId));
        requests.add(buildProductSkusRequest(productId));
        requests.add(buildProductCommentsRequest(productId));
        requests.add(buildProductServerRequest(productId));
        requests.add(buildProductCustomersRequest(productId));

        return requests;
    }

    private MomiaHttpRequest buildProductInfoRequest(long productId) {
        return new MomiaHttpGetRequest("product", true, baseServiceUrl("product", productId));
    }

    private MomiaHttpRequest buildProductSkusRequest(long productId) {
        return new MomiaHttpGetRequest("skus", true, baseServiceUrl("product", productId, "sku"));
    }

    private MomiaHttpRequest buildProductCommentsRequest(long productId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", 10); // TODO comment count
        return new MomiaHttpGetRequest("comments", false, baseServiceUrl("product", productId, "comment"), builder.build());
    }

    private MomiaHttpRequest buildProductServerRequest(long productId) {
        return new MomiaHttpGetRequest("server", true, baseServiceUrl("product", productId, "server"));
    }

    private MomiaHttpRequest buildProductCustomersRequest(long productId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", 10); // TODO customer count
        return new MomiaHttpGetRequest("customers", false, baseServiceUrl("product", productId, "customer"), builder.build());
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(@PathVariable long id) {
        return executeRequest(buildProductSkusRequest(id));
    }
}
