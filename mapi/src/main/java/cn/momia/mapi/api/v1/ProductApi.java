package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/product")
public class ProductApi extends AbstractApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductApi.class);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getProduct(HttpServletRequest request, @PathVariable long id) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        requests.add(buildProductInfoRequest(request, id));
        requests.add(buildProductSkuRequest(request, id));
        requests.add(buildProductPlaceRequest(request, id));
        requests.add(buildProductCommentRequest(request, id));
        requests.add(buildProductServerRequest(request, id));
        requests.add(buildProductCustomerRequest(request, id));
        MomiaHttpResponseCollector collector = new MomiaHttpResponseCollector();
        List<Throwable> exceptions = new ArrayList<Throwable>();
        boolean successful = requestExecutor.execute(requests, collector, exceptions, requests.size());
        if (!successful) {
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to get product");
        }

        return new ResponseMessage(buildProductResponse(collector));
    }

    private MomiaHttpRequest buildProductInfoRequest(HttpServletRequest request, long productId) {
        return buildMomiaHttpGetRequest(request, productId, "product", true, "");
    }

    private MomiaHttpRequest buildMomiaHttpGetRequest(HttpServletRequest request, long productId, String name, boolean required, String path) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(conf.getString("Service.Base")).append("/product/").append(productId).append(path);
        Map<String, String> params = new HashMap<String, String>();
        extractBasicParams(request, params);
        addSign(request, params);

        return new MomiaHttpGetRequest(name, required, urlBuilder.toString(), params);
    }

    private MomiaHttpRequest buildProductSkuRequest(HttpServletRequest request, long productId) {
        return buildMomiaHttpGetRequest(request, productId, "sku", true, "/sku");
    }

    private MomiaHttpRequest buildProductPlaceRequest(HttpServletRequest request, long productId) {
        return buildMomiaHttpGetRequest(request, productId, "place", true, "/place");
    }

    private MomiaHttpRequest buildProductCommentRequest(HttpServletRequest request, long productId) {
        return buildMomiaHttpGetRequest(request, productId, "comment", false, "/comment");
    }

    private MomiaHttpRequest buildProductServerRequest(HttpServletRequest request, long productId) {
        return buildMomiaHttpGetRequest(request, productId, "server", true, "/server");
    }

    private MomiaHttpRequest buildProductCustomerRequest(HttpServletRequest request, long productId) {
        return buildMomiaHttpGetRequest(request, productId, "customer", false, "/customer");
    }

    private JSONObject buildProductResponse(MomiaHttpResponseCollector collector) {
        JSONObject productObject = new JSONObject();
        productObject.put("product", collector.get("product"));
        productObject.put("sku", collector.get("sku"));
        productObject.put("place", collector.get("place"));
        productObject.put("server", collector.get("server"));

        JSONObject comments = collector.get("comment");
        if (comments != null) productObject.put("comment", comments);

        JSONObject customers = collector.get("customer");
        if (customers != null) productObject.put("customer", customers);

        return productObject;
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(HttpServletRequest request, @PathVariable long id) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(conf.getString("Service.Base")).append("/product/").append(id).append("/sku");
        Map<String, String> params = new HashMap<String, String>();
        extractBasicParams(request, params);
        addSign(request, params);
        try {
            JSONObject jsonResponse = httpClient.execute(new MomiaHttpPostRequest("sku", true, urlBuilder.toString(), params));
            return ResponseMessage.formJson(jsonResponse);
        } catch (Exception e) {
            LOGGER.error("fail to get skus of product: {}", id, e);
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to get skus of product");
        }
    }
}
