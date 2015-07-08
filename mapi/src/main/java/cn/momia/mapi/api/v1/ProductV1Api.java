package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.base.ContactsDto;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.PlayMateDto;
import cn.momia.mapi.api.v1.dto.base.ProductDto;
import cn.momia.mapi.api.v1.dto.base.SkuDto;
import cn.momia.mapi.api.v1.dto.composite.ListDto;
import cn.momia.mapi.api.v1.dto.composite.PagedListDto;
import cn.momia.mapi.api.v1.dto.composite.ProductDetailDto;
import cn.momia.mapi.api.v1.dto.composite.PlaceOrderDto;
import cn.momia.mapi.img.ImageFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/product")
public class ProductV1Api extends AbstractV1Api {
    private static final Function<Object, Dto> PlaymateFunc = new Function<Object, Dto>() {
        @Override
        public Dto apply(Object data) {
            JSONArray jsonArray = (JSONArray) data;
            PagedListDto<PlayMateDto> playerMateDtoPagedListDto = new PagedListDto<PlayMateDto>();
            for(int i=0; i<jsonArray.size(); i++)
                playerMateDtoPagedListDto.add(new PlayMateDto(jsonArray.getJSONObject(i)));
            playerMateDtoPagedListDto.setTotalCount(jsonArray.size());
            return playerMateDtoPagedListDto;
        }
    };
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
                PagedListDto<ProductDto> products = new PagedListDto<ProductDto>();

                JSONObject productsPackJson = (JSONObject) data;
                long totalCount = productsPackJson.getLong("totalCount");
                products.setTotalCount(totalCount);
                JSONArray productsJson = productsPackJson.getJSONArray("products");
                products.addAll(ProductUtil.extractProductsData(productsJson));
                if (start + count < totalCount) products.setNextIndex(start + count);

                return products;
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getProduct(@RequestParam long id) {
        if (id <= 0) return ResponseMessage.BAD_REQUEST;

        List<MomiaHttpRequest> requests = buildProductRequests(id);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Dto>() {
            @Override
            public Dto apply(MomiaHttpResponseCollector collector) {
                ProductDetailDto productDetail = new ProductDetailDto();

                JSONObject productJson = (JSONObject) collector.getResponse("product");
                ProductDto product = ProductUtil.extractProductData(productJson, true);
                productDetail.setProductDto(product);

                JSONArray customersJson = (JSONArray) collector.getResponse("customers");
                productDetail.setCustomers(getCustomers(customersJson));

                return productDetail;
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

    private ProductDetailDto.Customers getCustomers(JSONArray customersJson) {
        ProductDetailDto.Customers customers = new ProductDetailDto.Customers();

        int childCount = 0;
        int adultCount = 0;

        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);
        for (int i = 0; i < customersJson.size(); i++) {
            JSONObject customerJson = customersJson.getJSONObject(i);
            if (customers.avatars == null) customers.avatars = new ArrayList<String>();
            customers.avatars.add(ImageFile.url(customerJson.getString("avatar")));
            JSONArray participantsJson = customerJson.getJSONArray("participants");
            for (int j = 0; j < participantsJson.size(); j++) {
                Date birthday = participantsJson.getJSONObject(j).getDate("birthday");
                calendar.setTime(birthday);
                int yearBorn = calendar.get(Calendar.YEAR);
                if (yearNow - yearBorn > 15) adultCount++;
                else childCount++;
            }
        }

        if (childCount == 0 && adultCount == 0) customers.text = "目前还没有人参加";
        else if (childCount > 0 && adultCount == 0) customers.text = childCount + "个孩子参加";
        else if (childCount == 0 && adultCount > 0) customers.text = adultCount + "个大人参加";
        else customers.text = childCount + "个孩子，" + adultCount + "个大人参加";

        return customers;
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseMessage getProductOrder(@RequestParam String utoken, @RequestParam long id) {
        if(StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;
        
        List<MomiaHttpRequest> requests = buildProductOrderRequests(id, utoken);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Dto>() {
            @Override
            public Dto apply(MomiaHttpResponseCollector collector) {
                PlaceOrderDto placeOrderDto = new PlaceOrderDto();
                placeOrderDto.setContacts(getContacts((JSONObject) collector.getResponse("contacts")));
                placeOrderDto.setSkus(getSkus((JSONArray) collector.getResponse("skus")));

                return placeOrderDto;
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

    private ContactsDto getContacts(JSONObject userPackJson) {
        if (userPackJson == null) return null;

        JSONObject userJson = userPackJson.getJSONObject("user");
        if (userJson == null) return null;

        return new ContactsDto(userJson);
    }

    private ListDto getSkus(JSONArray skusJson) {
        ListDto skus = new ListDto();

        for (int i = 0; i < skusJson.size(); i++) {
            skus.add(new SkuDto(skusJson.getJSONObject(i)));
        }

        return skus;
    }

    @RequestMapping(value = "customer", method = RequestMethod.GET)
    public ResponseMessage getProductPlaymates(@RequestParam long id) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", conf.getInt("Product.MaxPageCount"));
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("product", id, "playmates"), builder.build());
        return executeRequest(request, PlaymateFunc);
    }
}
