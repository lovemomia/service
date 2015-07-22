package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.home.BannerDto;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.home.HomeDto;
import cn.momia.mapi.api.v1.dto.base.ListDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/home")
public class HomeV1Api extends AbstractV1Api {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeV1Api.class);

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage home(@RequestParam(value = "pageindex") final int pageIndex,
                                @RequestParam(value = "city") int cityId) {
        if (pageIndex < 0 || cityId < 0) return new ResponseMessage(HomeDto.EMPTY);

        final int start = pageIndex * conf.getInt("Home.PageSize");
        final int count = conf.getInt("Home.PageSize");
        List<MomiaHttpRequest> requests = buildHomeRequests(cityId, start, count);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Dto>() {
            @Override
            public Dto apply(MomiaHttpResponseCollector collector) {
                return buildHomeDto(collector, start, count, pageIndex);
            }
        });
    }

    private List<MomiaHttpRequest> buildHomeRequests(int cityId, int start, int count) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        if (start == 0) requests.add(buildBannersRequest(cityId));
        requests.add(buildProductsRequest(cityId, start, count));

        return requests;
    }

    private MomiaHttpRequest buildBannersRequest(int cityId) {
        int count = conf.getInt("Home.BannerCount");
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("count", count);

        return MomiaHttpRequest.GET("banners", true, url("banner"), builder.build());
    }

    private MomiaHttpRequest buildProductsRequest(int cityId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", start)
                .add("count", count);

        return MomiaHttpRequest.GET("products", true, url("product"), builder.build());
    }

    private Dto buildHomeDto(MomiaHttpResponseCollector collector, int start, int count, int pageIndex) {
        HomeDto homeDto = new HomeDto();

        if (start == 0) homeDto.setBanners(extractBannerData((JSONArray) collector.getResponse("banners")));

        JSONObject productsPackJson = (JSONObject) collector.getResponse("products");

        JSONArray productsJson = productsPackJson.getJSONArray("products");
        homeDto.setProducts(ProductUtil.extractProductsData(productsJson));

        long totalCount = productsPackJson.getLong("totalCount");
        if (start + count < totalCount) homeDto.setNextpage(pageIndex + 1);

        return homeDto;
    }

    private ListDto extractBannerData(JSONArray bannersJson) {
        ListDto banners = new ListDto();
        for (int i = 0; i < bannersJson.size(); i++) {
            try {
                banners.add(new BannerDto(bannersJson.getJSONObject(i)));
            } catch (Exception e) {
                LOGGER.error("fail to parser the {}th banner", i, e);
            }
        }

        return banners;
    }
}
