package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/product")
public class ProductApi extends AbstractApi {
    private static final DateFormat YEAR_DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("M月d日");
    private static final String[] WEEK_DAYS = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };

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

                JSONObject baseProduct = (JSONObject) collector.getResponse("product");
                productObject.put("id", baseProduct.get("id"));
                productObject.put("cover", baseProduct.get("cover"));
                productObject.put("title", baseProduct.get("title"));
                productObject.put("joined", baseProduct.get("sales"));
                productObject.put("imgs", baseProduct.getJSONArray("imgs"));
                productObject.put("content", baseProduct.getJSONObject("content"));

                processPlace(productObject, collector);
                processSkus(productObject, collector);
                processComments(productObject, collector);
                processCustomers(productObject, collector);

                return productObject;
            }
        });
    }

    private List<MomiaHttpRequest> buildProductRequests(long productId) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        requests.add(buildProductInfoRequest(productId));
        requests.add(buildProductPlaceRequest(productId));
        requests.add(buildProductSkusRequest(productId));
        requests.add(buildProductCommentsRequest(productId));
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

    private MomiaHttpRequest buildProductCommentsRequest(long productId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", conf.getInt("Product.CommentPageSize"));
        return new MomiaHttpGetRequest("comments", false, baseServiceUrl("product", productId, "comment"), builder.build());
    }

    private MomiaHttpRequest buildProductCustomersRequest(long productId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", 0)
                .add("count", conf.getInt("Product.CustomerPageSize"));
        return new MomiaHttpGetRequest("customers", false, baseServiceUrl("product", productId, "customer"), builder.build());
    }

    private void processPlace(JSONObject productObject, MomiaHttpResponseCollector collector) {
        Object place = collector.getResponse("place");
        if (place != null) {
            JSONObject placeJson = (JSONObject) place;
            productObject.put("address", placeJson.get("address"));
            productObject.put("poi", StringUtils.join(new Object[] { placeJson.getFloat("lng"), placeJson.getFloat("lat") }, ":"));
            productObject.put("placeImgs", placeJson.getJSONArray("imgs"));
        }
    }

    private void processSkus(JSONObject productObject, MomiaHttpResponseCollector collector) {
        JSONArray skus = (JSONArray) collector.getResponse("skus");

        List<Float> prices = new ArrayList<Float>();
        List<Date> times = new ArrayList<Date>();
        for (int j = 0; j < skus.size(); j++) {
            JSONObject sku = skus.getJSONObject(j);
            prices.add(sku.getFloat("price"));
            JSONArray proterties = sku.getJSONArray("properties");
            for (int k = 0; k < proterties.size(); k++) {
                JSONObject property = proterties.getJSONObject(k);
                if (property.getString("name").equals("时间")) {
                    times.add(property.getDate("value"));
                }
            }
        }

        Collections.sort(prices);
        Collections.sort(times);

        if (!prices.isEmpty()) productObject.put("price", prices.get(0));
        productObject.put("time", buildTimeString(times));

        productObject.put("skus", formatSkus(skus));
    }

    private String buildTimeString(List<Date> times) {

        if (times.isEmpty()) return "";
        if (times.size() == 1) {
            Date start = times.get(0);
            return DATE_FORMATTER.format(start) + " " + getWeekDay(start) + " 共1场";
        } else {
            Date start = times.get(0);
            Date end = times.get(times.size() - 1);
            if (isSameDay(start, end)) {
                return DATE_FORMATTER.format(start) + " " + getWeekDay(start) + " 共" + times.size() + "场";
            } else {
                return DATE_FORMATTER.format(start) + "-" + DATE_FORMATTER.format(end) + " " + getWeekDay(start) + "-" + getWeekDay(end) + " 共" + times.size() + "场";
            }
        }
    }

    private String getWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return WEEK_DAYS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    private boolean isSameDay(Date start, Date end) {
        return YEAR_DATE_FORMATTER.format(start).equals(YEAR_DATE_FORMATTER.format(end));
    }

    private JSONArray formatSkus(JSONArray skus) {
        JSONArray formattedSkus = new JSONArray();
        for (int i = 0; i < skus.size(); i++) {
            JSONObject sku = skus.getJSONObject(i);
            JSONObject formattedSku = new JSONObject();
            formattedSku.put("id", sku.get("id"));
            formattedSku.put("price", sku.getFloat("price"));
            formattedSku.put("stock", sku.getInteger("unlockedStock"));
            formattedSku.put("properties", sku.getJSONArray("properties"));

            formattedSkus.add(formattedSku);
        }

        return formattedSkus;
    }

    private void processComments(JSONObject productObject, MomiaHttpResponseCollector collector) {
        Object comments = collector.getResponse("comments");
        if (comments != null) productObject.put("comments", comments);
    }

    private void processCustomers(JSONObject productObject, MomiaHttpResponseCollector collector ) {
        Object customers = collector.getResponse("customers");
        if (customers != null) productObject.put("customers", customers);
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(@PathVariable long id) {
        return executeRequest(buildProductSkusRequest(id));
    }
}
