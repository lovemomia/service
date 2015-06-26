package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.ProductDto;
import cn.momia.mapi.api.v1.dto.composite.ListDto;
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

        return executeRequest(MomiaHttpRequest.POST(baseServiceUrl("favorite"), builder.build()));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseMessage deleteFavorite(@RequestParam long id, @RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.DELETE(baseServiceUrl("favorite", id), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getFavoritesOfUser(@RequestParam String utoken, @RequestParam int start, @RequestParam int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("favorite"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                ListDto favorites = new ListDto();

                JSONArray productsJson = (JSONArray) data;
                for (int i = 0; i < productsJson.size(); i++) {
                    ProductDto product = new ProductDto();

                    JSONObject productJson = productsJson.getJSONObject(i);
                    JSONObject baseProductJson = productJson.getJSONObject("product");
                    JSONArray skusJson = productJson.getJSONArray("skus");

                    product.setId(baseProductJson.getLong("id"));
                    product.setCover(baseProductJson.getString("cover"));
                    product.setTitle(baseProductJson.getString("title"));
                    product.setScheduler(ProductUtil.getScheduler(skusJson));
                    product.setJoined(baseProductJson.getInteger("sales"));
                    product.setPrice(ProductUtil.getMiniPrice(skusJson));

                    favorites.add(product);
                }

                return favorites;
            }
        });
    }
}
