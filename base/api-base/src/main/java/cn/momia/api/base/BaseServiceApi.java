package cn.momia.api.base;

import cn.momia.api.base.dto.RegionDto;
import cn.momia.api.base.dto.CityDto;
import cn.momia.api.base.dto.CityDistrictsDto;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class BaseServiceApi extends AbstractServiceApi {
    public static CityServiceApi CITY = new CityServiceApi();
    public static RegionServiceApi REGION = new RegionServiceApi();
    public static FeedbackServiceApi FEEDBACK = new FeedbackServiceApi();
    public static RecommendServiceApi RECOMMEND = new RecommendServiceApi();
    public static TicketServiceApi TICKET = new TicketServiceApi();
    public static SmsServiceApi SMS = new SmsServiceApi();

    public void init() {
        CITY.setService(service);
        REGION.setService(service);
        FEEDBACK.setService(service);
        RECOMMEND.setService(service);
        SMS.setService(service);
    }

    public static class CityServiceApi extends BaseServiceApi {
        public List<CityDto> listAll() {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("city"));
            return CastUtil.toList((JSONArray) executeRequest(request), CityDto.class);
        }
    }

    public static class RegionServiceApi extends BaseServiceApi {
        public List<RegionDto> listAll() {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("region"));
            return CastUtil.toList((JSONArray) executeRequest(request), RegionDto.class);
        }

        public List<CityDistrictsDto> listAllCityDistricts() {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("region/district/tree"));
            return CastUtil.toList((JSONArray) executeRequest(request), CityDistrictsDto.class);
        }
    }

    public static class FeedbackServiceApi extends BaseServiceApi {
        public boolean addFeedback(String content, String email) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("content", content)
                    .add("email", email);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("feedback"), builder.build());
            return (Boolean) executeRequest(request);
        }
    }

    public static class RecommendServiceApi extends BaseServiceApi {
        public boolean addRecommend(String content, String time, String address, String contacts) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("content", content)
                    .add("time", time)
                    .add("address", address)
                    .add("contacts", contacts);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("recommend"), builder.build());
            return (Boolean) executeRequest(request);
        }
    }

    public static class TicketServiceApi extends BaseServiceApi {
        public String generate() {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("ticket"));
            return (String) executeRequest(request);
        }
    }

    public static class SmsServiceApi extends BaseServiceApi {
        public boolean send(String mobile, String type) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("type", type);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("sms/send"), builder.build());
            return (Boolean) executeRequest(request);
        }

        public boolean verify(String mobile, String code) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("code", code);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("sms/verify"), builder.build());
            return (Boolean) executeRequest(request);
        }

        public boolean notify(String mobile, String msg) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("mobile", mobile)
                    .add("msg", msg);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("sms/notify"), builder.build());
            return (Boolean) executeRequest(request);
        }
    }
}
