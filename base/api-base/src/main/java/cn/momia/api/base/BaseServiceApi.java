package cn.momia.api.base;

import cn.momia.api.base.region.Region;
import cn.momia.api.base.city.City;
import cn.momia.api.base.region.CityDistricts;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BaseServiceApi extends AbstractServiceApi {
    public static CityServiceApi CITY = new CityServiceApi();
    public static RegionServiceApi REGION = new RegionServiceApi();
    public static FeedbackServiceApi FEEDBACK = new FeedbackServiceApi();
    public static RecommendServiceApi RECOMMEND = new RecommendServiceApi();
    public static SmsServiceApi SMS = new SmsServiceApi();

    public void init() {
        CITY.setService(service);
        REGION.setService(service);
        FEEDBACK.setService(service);
        RECOMMEND.setService(service);
        SMS.setService(service);
    }

    public static class CityServiceApi extends BaseServiceApi {
        public List<City> getAll() {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("city"));
            JSONArray citiesJson = (JSONArray) executeRequest(request);

            List<City> cities = new ArrayList<City>();
            for (int i = 0; i < citiesJson.size(); i++) {
                JSONObject cityJson = citiesJson.getJSONObject(i);
                cities.add(JSON.toJavaObject(cityJson, City.class));
            }

            return cities;
        }
    }

    public static class RegionServiceApi extends BaseServiceApi {
        public List<Region> getAll() {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("region"));
            JSONArray regionsJson = (JSONArray) executeRequest(request);

            List<Region> regions = new ArrayList<Region>();
            for (int i = 0; i < regionsJson.size(); i++) {
                JSONObject regionJson = regionsJson.getJSONObject(i);
                regions.add(JSON.toJavaObject(regionJson, Region.class));
            }

            return regions;
        }

        public List<CityDistricts> getCityDistrictTree() {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("region/district/tree"));
            JSONArray citiesDistrictsJson = (JSONArray) executeRequest(request);

            List<CityDistricts> citiesDistricts = new ArrayList<CityDistricts>();
            for (int i = 0; i < citiesDistrictsJson.size(); i++) {
                JSONObject cityDistrictsJson = citiesDistrictsJson.getJSONObject(i);
                citiesDistricts.add(JSON.toJavaObject(cityDistrictsJson, CityDistricts.class));
            }

            return citiesDistricts;
        }
    }

    public static class FeedbackServiceApi extends BaseServiceApi {
        public void addFeedback(String content, String email) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("content", content)
                    .add("email", email);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("feedback"), builder.build());
            executeRequest(request);
        }
    }

    public static class RecommendServiceApi extends BaseServiceApi {
        public void addRecommend(String content, String time, String address, String contacts) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("content", content)
                    .add("time", time)
                    .add("address", address)
                    .add("contacts", contacts);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("recommend"), builder.build());
            executeRequest(request);
        }
    }

    public static class SmsServiceApi extends BaseServiceApi {
        public void send(String mobile, String type) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("type", type);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("sms/send"), builder.build());
            executeRequest(request);
        }

        public void verify(String mobile, String code) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("code", code);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("sms/verify"), builder.build());
            executeRequest(request);
        }

        public void notify(String mobile, String msg) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("msg", msg);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("sms/notify"), builder.build());
            executeRequest(request);
        }
    }
}
