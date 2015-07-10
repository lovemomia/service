package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.composite.PagedListDto;
import cn.momia.mapi.api.v1.dto.composite.ProductDetailDto;
import cn.momia.mapi.api.v1.dto.composite.PlaceOrderDto;
import com.alibaba.fastjson.JSONArray;
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
@RequestMapping("/v1/product")
public class ProductV1Api extends AbstractV1Api {
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseMessage getProducts(@RequestParam(value = "city") final int cityId,
                                       @RequestParam final int start,
                                       @RequestParam final int count,
                                       @RequestParam(required = false) String query) {
        final int maxPageCount = conf.getInt("Product.MaxPageCount");
        final int pageSize = conf.getInt("Product.PageSize");
        if (cityId < 0 || start < 0 || count <= 0 || start > maxPageCount * pageSize) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count)
                .add("query", query);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("product"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return buildProductsDto((JSONObject) data, start, count);
            }
        });
    }

    private Dto buildProductsDto(JSONObject productsPackJson, int start, int count) {
        PagedListDto products = new PagedListDto();

        long totalCount = productsPackJson.getLong("totalCount");
        products.setTotalCount(totalCount);
        if (start + count < totalCount) products.setNextIndex(start + count);

        JSONArray productsJson = productsPackJson.getJSONArray("products");
        products.addAll(ProductUtil.extractProductsData(productsJson));

        return products;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getProduct(@RequestParam final long id) {
        if (id <= 0) return ResponseMessage.BAD_REQUEST;

        List<MomiaHttpRequest> requests = buildProductRequests(id);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Dto>() {
            @Override
            public Dto apply(MomiaHttpResponseCollector collector) {
                ProductDetailDto productDetailDto = new ProductDetailDto((JSONObject) collector.getResponse("product"), (JSONObject) collector.getResponse("customers"));
                productDetailDto.setUrl(conf.getString("Product.Url") + "?id=" + id);
                return productDetailDto;
            }
        });
    }

    private List<MomiaHttpRequest> buildProductRequests(long productId) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        requests.add(buildProductRequest(productId));
        requests.add(buildProductCustomersRequest(productId));

        return requests;
    }

    private MomiaHttpRequest buildProductRequest(long productId) {
        return MomiaHttpRequest.GET("product", true, baseServiceUrl("product", productId));
    }

    private MomiaHttpRequest buildProductCustomersRequest(long productId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", conf.getInt("Product.CustomerPageSize"));

        return MomiaHttpRequest.GET("customers", false, baseServiceUrl("product", productId, "customer"), builder.build());
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseMessage getProductOrder(@RequestParam String utoken, @RequestParam long id) {
        if(StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;
        
        List<MomiaHttpRequest> requests = buildProductOrderRequests(id, utoken);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Dto>() {
            @Override
            public Dto apply(MomiaHttpResponseCollector collector) {
                return new PlaceOrderDto((JSONObject) collector.getResponse("contacts"), (JSONArray) collector.getResponse("skus"));
            }
        });
    }

    private List<MomiaHttpRequest> buildProductOrderRequests(long productId, String utoken) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        requests.add(buildProductSkusRequest(productId));
        requests.add(buildUserRequest(utoken));

        return requests;
    }

    private MomiaHttpRequest buildProductSkusRequest(long productId) {
        return MomiaHttpRequest.GET("skus", true, baseServiceUrl("product", productId, "sku"));
    }

    private MomiaHttpRequest buildUserRequest(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET("contacts", true, baseServiceUrl("user"), builder.build());

        return request;
    }

    @RequestMapping(value = "/playmate", method = RequestMethod.GET)
    public ResponseMessage getProductPlaymates(@RequestParam long id) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", conf.getInt("Product.Playmate.MaxSkuCount"));
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("product", id, "playmate"), builder.build());

        return executeRequest(request);
    }
}
