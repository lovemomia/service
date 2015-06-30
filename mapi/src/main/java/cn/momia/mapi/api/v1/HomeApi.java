package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.base.BannerDto;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.composite.HomeDto;
import cn.momia.mapi.api.v1.dto.base.ProductDto;
import cn.momia.mapi.img.ImageFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
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
public class HomeApi extends AbstractApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeApi.class);

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage home(@RequestParam(value = "pageindex") final int pageIndex, @RequestParam(value = "city") int cityId) {
        final int maxPageCount = conf.getInt("Home.MaxPageCount");
        final int pageSize = conf.getInt("Home.PageSize");
        if (pageIndex >= maxPageCount) return ResponseMessage.FAILED;

        List<MomiaHttpRequest> requests = buildHomeRequests(pageIndex, cityId);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Dto>() {
            @Override
            public Dto apply(MomiaHttpResponseCollector collector) {
                HomeDto homeDto = new HomeDto();

                if (pageIndex == 0) homeDto.setBanners(extractBannerData((JSONArray) collector.getResponse("banners")));

                JSONObject productsPackJson = (JSONObject) collector.getResponse("products");
                long totalCount = productsPackJson.getLong("totalCount");
                JSONArray productsJson = productsPackJson.getJSONArray("products");
                List<ProductDto> products = extractProductsData(productsJson);
                homeDto.setProducts(products);
                if (pageIndex < maxPageCount - 1 &&
                        (pageIndex + 1) * pageSize < totalCount) homeDto.setNextpage(pageIndex + 1);

                return homeDto;
            }
        });
    }

    private List<MomiaHttpRequest> buildHomeRequests(int pageIndex, int cityId) {
        List<MomiaHttpRequest> requests = new ArrayList<MomiaHttpRequest>();
        if (pageIndex == 0) requests.add(buildBannersRequest(cityId));
        requests.add(buildProductsRequest(pageIndex, cityId));

        return requests;
    }

    private MomiaHttpRequest buildBannersRequest(int cityId) {
        int count = conf.getInt("Home.BannerCount");
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("count", count);
        return MomiaHttpRequest.GET("banners", true, baseServiceUrl("banner"), builder.build());
    }

    private MomiaHttpRequest buildProductsRequest(int pageIndex, int cityId) {
        int pageSize = conf.getInt("Home.PageSize");
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", String.valueOf(pageIndex * pageSize))
                .add("count", String.valueOf(pageSize));

        return MomiaHttpRequest.GET("products", true, baseServiceUrl("product"), builder.build());
    }

    private List<BannerDto> extractBannerData(JSONArray bannersJson) {
        List<BannerDto> banners = new ArrayList<BannerDto>();

        for (int i = 0; i < bannersJson.size(); i++) {
            JSONObject bannerJson = bannersJson.getJSONObject(i);
            BannerDto banner = new BannerDto();
            banner.setCover(ImageFile.url(bannerJson.getString("cover")));
            banner.setAction(bannerJson.getString("action"));

            banners.add(banner);
        }

        return banners;
    }

    private List<ProductDto> extractProductsData(JSONArray productsJson) {
        List<ProductDto> products = new ArrayList<ProductDto>();

        for (int i = 0; i < productsJson.size(); i++) {
            try {
                ProductDto product = new ProductDto();

                JSONObject productJson = productsJson.getJSONObject(i);
                JSONObject placeJson = productJson.getJSONObject("place");
                JSONArray skusJson = productJson.getJSONArray("skus");

                product.setId(productJson.getLong("id"));
                product.setCover(ImageFile.url(productJson.getString("cover")));
                product.setTitle(productJson.getString("title"));
                product.setAddress(placeJson.getString("address"));
                product.setPoi(StringUtils.join(new Object[] { placeJson.getDouble("lng"), placeJson.getDouble("lat") }, ":"));
                product.setScheduler(ProductUtil.getScheduler(skusJson));
                product.setJoined(productJson.getInteger("sales"));
                product.setPrice(ProductUtil.getMiniPrice(skusJson));

                products.add(product);
            } catch (Exception e) {
                LOGGER.error("fail to parse product: ", productsJson.getJSONObject(i), e);
            }
        }

        return products;
    }
}
