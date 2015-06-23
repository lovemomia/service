package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.Dto;
import cn.momia.mapi.api.v1.dto.HomeDto;
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
import java.util.List;

@RestController
@RequestMapping("/v1/home")
public class HomeApi extends AbstractApi {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage home(@RequestParam(value = "pageindex") final int pageIndex, @RequestParam(value = "city") int cityId) {
        List<MomiaHttpRequest> requests = buildHomeRequests(pageIndex, cityId);

        return executeRequests(requests, new Function<MomiaHttpResponseCollector, Dto>() {
            @Override
            public Dto apply(MomiaHttpResponseCollector collector) {
                HomeDto homeDto = new HomeDto();

                if (pageIndex == 0) homeDto.setBanners(extractBannerData((JSONArray) collector.getResponse("banners")));

                List<HomeDto.Product> products = extractProductsData((JSONArray) collector.getResponse("products"));
                homeDto.setProducts(products);
                if (products.size() == conf.getInt("Home.PageSize")) homeDto.setNextpage(pageIndex + 1);

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
        return new MomiaHttpGetRequest("banners", true, baseServiceUrl("banner"), builder.build());
    }

    private MomiaHttpRequest buildProductsRequest(int pageIndex, int cityId) {
        int pageSize = conf.getInt("Home.PageSize");
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("start", String.valueOf(pageIndex * pageSize))
                .add("count", String.valueOf(pageSize));

        return new MomiaHttpGetRequest("products", true, baseServiceUrl("product"), builder.build());
    }

    private List<HomeDto.Banner> extractBannerData(JSONArray bannerArray) {
        List<HomeDto.Banner> banners = new ArrayList<HomeDto.Banner>();

        for (int i = 0; i < bannerArray.size(); i++) {
            JSONObject bannerObject = bannerArray.getJSONObject(i);
            HomeDto.Banner banner = new HomeDto.Banner();
            banner.setCover(ImageFile.url(bannerObject.getString("cover")));
            banner.setAction(bannerObject.getString("action"));

            banners.add(banner);
        }

        return banners;
    }

    private List<HomeDto.Product> extractProductsData(JSONArray productArray) {
        List<HomeDto.Product> products = new ArrayList<HomeDto.Product>();

        for (int i = 0; i < productArray.size(); i++) {
            HomeDto.Product product = new HomeDto.Product();

            JSONObject productObject = productArray.getJSONObject(i);
            JSONObject baseProduct = productObject.getJSONObject("product");
            JSONObject place = productObject.getJSONObject("place");
            JSONArray skus = productObject.getJSONArray("skus");

            product.setId(baseProduct.getLong("id"));
            product.setCover(ImageFile.url(baseProduct.getString("cover")));
            product.setTitle(baseProduct.getString("title"));
            product.setAddress(place.getString("address"));
            product.setPoi(StringUtils.join(new Object[] { place.getFloat("lng"), place.getFloat("lat") }, ":"));
            product.setScheduler(ProductUtil.getScheduler(skus));
            product.setJoined(baseProduct.getInteger("sales"));
            product.setPrice(ProductUtil.getPrice(skus));

            products.add(product);
        }

        return products;
    }
}
