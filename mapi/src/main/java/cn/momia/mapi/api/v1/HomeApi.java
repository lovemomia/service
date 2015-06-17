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
@RequestMapping("/v1/home")
public class HomeApi extends AbstractApi {
    private static final DateFormat YEAR_DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("M月d日");
    private static final String[] WEEK_DAYS = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage home(@RequestParam(value = "pageindex") final int pageIndex) {
        List<MomiaHttpRequest> requests = buildHomeRequests(pageIndex);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, JSONObject>() {
            @Override
            public JSONObject apply(MomiaHttpResponseCollector collector) {
                JSONObject homeData = new JSONObject();
                if (pageIndex == 0) homeData.put("banners", collector.getResponse("banners"));
                JSONArray products = extractProductsData((JSONArray) collector.getResponse("products"));
                homeData.put("products", products);
                if (products.size() == conf.getInt("Home.PageSize")) homeData.put("nextpage", pageIndex + 1);

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
        int count = conf.getInt("Home.BannerCount");
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("count", count);
        return new MomiaHttpGetRequest("banners", true, baseServiceUrl("banner"), builder.build());
    }

    private MomiaHttpRequest buildProductsRequest(int pageIndex) {
        int pageSize = conf.getInt("Home.PageSize");
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("start", String.valueOf(pageIndex * pageSize))
                .add("count", String.valueOf(pageSize));

        return new MomiaHttpGetRequest("products", true, baseServiceUrl("product"), builder.build());
    }

    private JSONArray extractProductsData(JSONArray rawProducts) {
        JSONArray products = new JSONArray();

        for (int i = 0; i < rawProducts.size(); i++) {
            JSONObject rawProduct = rawProducts.getJSONObject(i);

            JSONObject rawBaseProduct = rawProduct.getJSONObject("product");
            JSONObject product = new JSONObject();
            product.put("id", rawBaseProduct.get("id"));
            product.put("cover", rawBaseProduct.get("cover"));
            product.put("title", rawBaseProduct.get("title"));
            product.put("joined", rawBaseProduct.get("sales"));

            JSONObject place = rawProduct.getJSONObject("place");
            if (place != null) {
                product.put("address", place.get("address"));
                product.put("poi", StringUtils.join(new Object[] { place.getFloat("lng"), place.getFloat("lat") }, ":"));
            }

            JSONArray skus = rawProduct.getJSONArray("skus");
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

            if (!prices.isEmpty()) product.put("price", prices.get(0));
            product.put("scheduler", buildScheduler(times));

            products.add(product);
        }
        return products;
    }

    private String buildScheduler(List<Date> times) {

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
}
