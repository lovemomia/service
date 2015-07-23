package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.img.ImageFile;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.base.ListDto;
import cn.momia.mapi.api.v1.dto.product.PlaymatesDto;
import cn.momia.mapi.api.v1.dto.base.PagedListDto;
import cn.momia.mapi.api.v1.dto.product.PlaceOrderDto;
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
    @RequestMapping(value = "/weekend", method = RequestMethod.GET)
    public ResponseMessage getProductsByWeekend(@RequestParam(value = "city") int cityId, @RequestParam int start) {
        final int pageSize = conf.getInt("Product.PageSize");
        final int maxPageCount = conf.getInt("Product.MaxPageCount");
        if (cityId < 0 || start < 0 || start > pageSize * maxPageCount) return new ResponseMessage(PagedListDto.EMPTY);

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", pageSize);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/weekend"), builder.build());

        return executeRequest(request, pagedProductsFunc);
    }

    @RequestMapping(value = "/month", method = RequestMethod.GET)
    public ResponseMessage getProductsByMonth(@RequestParam(value = "city") int cityId, @RequestParam final int month) {
        if (cityId < 0) return new ResponseMessage(ListDto.EMPTY);

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("month", month);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/month"), builder.build());

        return executeRequest(request, new Function<Object, Object>() {
            @Override
            public Object apply(Object data) {
                JSONArray groupedProductsJson = (JSONArray) data;
                for (int i = 0; i < groupedProductsJson.size(); i++) {
                    productsFunc.apply(groupedProductsJson.getJSONObject(i).getJSONArray("products"));
                }

                return data;
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getProduct(@RequestParam final long id) {
        if (id <= 0) return ResponseMessage.BAD_REQUEST;

        List<MomiaHttpRequest> requests = buildProductRequests(id);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Object>() {
            @Override
            public Object apply(MomiaHttpResponseCollector collector) {
                JSONObject productJson = (JSONObject) productFunc.apply(collector.getResponse("product"));
                JSONObject customersJson = (JSONObject) collector.getResponse("customers");

                productJson.put("customers", processAvatars(customersJson));

                boolean opened = productJson.getBoolean("opened");
                if (!opened) productJson.put("soldOut", true);

                return productJson;
            }
        });
    }

    private JSONObject processAvatars(JSONObject customersJson) {
        JSONArray avatarsJson = customersJson.getJSONArray("avatars");
        if (avatarsJson != null) {
            for (int i = 0; i < avatarsJson.size(); i++) {
                String avatar = avatarsJson.getString(i);
                avatarsJson.set(i, ImageFile.url(avatar));
            }
        }

        return customersJson;
    }

    private List<MomiaHttpRequest> buildProductRequests(long productId) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        requests.add(buildProductRequest(productId));
        requests.add(buildProductCustomersRequest(productId));

        return requests;
    }

    private MomiaHttpRequest buildProductRequest(long productId) {
        return MomiaHttpRequest.GET("product", true, url("product", productId));
    }

    private MomiaHttpRequest buildProductCustomersRequest(long productId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", conf.getInt("Product.CustomerPageSize"));

        return MomiaHttpRequest.GET("customers", false, url("product", productId, "customer"), builder.build());
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseMessage getProductOrder(@RequestParam String utoken, @RequestParam long id) {
        if(StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;
        
        List<MomiaHttpRequest> requests = buildProductOrderRequests(id, utoken);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Object>() {
            @Override
            public Object apply(MomiaHttpResponseCollector collector) {
                return new PlaceOrderDto((JSONObject) collector.getResponse("contacts"), (JSONArray) collector.getResponse("skus"));
            }
        });
    }

    private List<MomiaHttpRequest> buildProductOrderRequests(long productId, String utoken) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        requests.add(buildContactsRequest(utoken));
        requests.add(buildProductSkusRequest(productId));

        return requests;
    }

    private MomiaHttpRequest buildContactsRequest(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET("contacts", true, url("user/contacts"), builder.build());

        return request;
    }

    private MomiaHttpRequest buildProductSkusRequest(long productId) {
        return MomiaHttpRequest.GET("skus", true, url("product", productId, "sku"));
    }

    @RequestMapping(value = "/playmate", method = RequestMethod.GET)
    public ResponseMessage getProductPlaymates(@RequestParam long id) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", conf.getInt("Product.Playmate.MaxSkuCount"));
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", id, "playmate"), builder.build());

        return executeRequest(request, new Function<Object, Object>() {
            @Override
            public Object apply(Object data) {
                return new PlaymatesDto((JSONArray) data);
            }
        });
    }

    @RequestMapping(value = "/favor", method = RequestMethod.POST)
    public ResponseMessage favor(@RequestParam String utoken, @RequestParam long id){
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("product", id, "favor"), builder.build());

        return executeRequest(request);
    }
}
