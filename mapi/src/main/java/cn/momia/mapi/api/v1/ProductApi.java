package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.Dto;
import cn.momia.mapi.api.v1.dto.ProductDto;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/product")
public class ProductApi extends AbstractApi {
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseMessage getProducts(@RequestParam int start, @RequestParam int count, @RequestParam String query) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", start)
                .add("count", count)
                .add("query", query);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("product"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getProduct(@RequestParam long id) {
        List<MomiaHttpRequest> requests = buildProductRequests(id);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Dto>() {
            @Override
            public Dto apply(MomiaHttpResponseCollector collector) {
                ProductDto productDto = new ProductDto();

                JSONObject baseProduct = (JSONObject) collector.getResponse("product");
                JSONObject place = (JSONObject) collector.getResponse("place");
                JSONArray skus = (JSONArray) collector.getResponse("skus");
                JSONArray customers = (JSONArray) collector.getResponse("customers");

                productDto.setId(baseProduct.getLong("id"));
                productDto.setCover(baseProduct.getString("cover"));
                productDto.setTitle(baseProduct.getString("title"));
                productDto.setJoined(baseProduct.getInteger("sales"));
                productDto.setPrice(ProductUtil.getPrice(skus));
                productDto.setCrowd(baseProduct.getString("crowd"));
                productDto.setScheduler(ProductUtil.getScheduler(skus));
                productDto.setAddress(place.getString("address"));
                productDto.setPoi(StringUtils.join(new Object[] { place.getFloat("lng"), place.getFloat("lat") }, ":"));
                productDto.setImgs(getImgs(baseProduct));
                productDto.setCustomers(getCustomers(customers));
                productDto.setContent(processImages(baseProduct.getJSONArray("content")));

                return productDto;
            }
        });
    }

    private List<MomiaHttpRequest> buildProductRequests(long productId) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        requests.add(buildProductInfoRequest(productId));
        requests.add(buildProductPlaceRequest(productId));
        requests.add(buildProductSkusRequest(productId));
        requests.add(buildProductCustomersRequest(productId));

        return requests;
    }

    private MomiaHttpRequest buildProductInfoRequest(long productId) {
        return new MomiaHttpGetRequest("product", true, baseServiceUrl("product", productId));
    }

    private MomiaHttpRequest buildProductPlaceRequest(long productId) {
        return new MomiaHttpGetRequest("place", true, baseServiceUrl("product", productId, "place"));
    }

    private MomiaHttpRequest buildProductSkusRequest(long productId) {
        return new MomiaHttpGetRequest("skus", true, baseServiceUrl("product", productId, "sku"));
    }

    private MomiaHttpRequest buildProductCustomersRequest(long productId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", conf.getInt("Product.CustomerPageSize"));
        return new MomiaHttpGetRequest("customers", false, baseServiceUrl("product", productId, "customer"), builder.build());
    }

    private List<String> getImgs(JSONObject baseProduct) {
        List<String> imgs = new ArrayList<String>();

        JSONArray imgArray = baseProduct.getJSONArray("imgs");
        for (int i = 0; i < imgArray.size(); i++) {
            imgs.add(ImageFile.url(imgArray.getJSONObject(i).getString("url")));
        }

        return imgs;
    }

    private ProductDto.Customers getCustomers(JSONArray customerArray) {
        ProductDto.Customers customers = new ProductDto.Customers();

        int childCount = 0;
        int adultCount = 0;

        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);
        for (int i = 0; i < customerArray.size(); i++) {
            JSONObject customer = customerArray.getJSONObject(i);
            if (customers.avatars == null) customers.avatars = new ArrayList<String>();
            customers.avatars.add(ImageFile.url(customer.getString("avatar")));
            JSONArray participants = customer.getJSONArray("participants");
            for (int j = 0; j < participants.size(); j++) {
                Date birthday = participants.getJSONObject(j).getDate("birthday");
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

    private JSONArray processImages(JSONArray content) {
        for (int i = 0; i < content.size(); i++) {
            JSONObject contentBlock = content.getJSONObject(i);
            JSONArray body = contentBlock.getJSONArray("body");
            for (int j = 0; j < body.size(); j++) {
                JSONObject bodyBlock = body.getJSONObject(j);
                String img = bodyBlock.getString("img");
                if (!StringUtils.isBlank(img)) bodyBlock.put("img", ImageFile.url(img));
            }
        }

        return content;
    }

    @RequestMapping(value = "/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(@RequestParam long id) {
        return executeRequest(buildProductSkusRequest(id));
    }
}
