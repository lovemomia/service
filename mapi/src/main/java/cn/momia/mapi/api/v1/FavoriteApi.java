package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.impl.MomiaHttpDeleteRequest;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.Dto;
import cn.momia.mapi.api.v1.dto.FavoriteDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/favorite")
public class FavoriteApi extends AbstractApi {
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addFavorite(@RequestParam String utoken, @RequestParam(value = "pid") long productId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("pid", productId);

        return executeRequest(new MomiaHttpPostRequest(baseServiceUrl("favorite"), builder.build()));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseMessage deleteFavorite(@RequestParam long id, @RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpDeleteRequest(baseServiceUrl("favorite", id), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getFavoritesOfUser(@RequestParam String utoken, @RequestParam int start, @RequestParam int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("favorite"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                FavoriteDto favoriteDto = new FavoriteDto();

                JSONArray productArray = (JSONArray) data;
                for (int i = 0; i < productArray.size(); i++) {
                    FavoriteDto.Product product = new FavoriteDto.Product();

                    JSONObject productObject = productArray.getJSONObject(i);
                    JSONObject baseProduct = productObject.getJSONObject("product");
                    JSONArray skus = productObject.getJSONArray("skus");

                    product.id = baseProduct.getLong("id");
                    product.cover = baseProduct.getString("cover");
                    product.title = baseProduct.getString("title");
                    product.scheduler = ProductUtil.getScheduler(skus);
                    product.joined = baseProduct.getInteger("sales");
                    product.price = ProductUtil.getPrice(skus);

                    favoriteDto.add(product);
                }

                return favoriteDto;
            }
        });
    }
}
