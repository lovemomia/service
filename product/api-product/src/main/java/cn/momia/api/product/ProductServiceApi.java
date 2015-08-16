package cn.momia.api.product;

import cn.momia.api.base.ServiceApi;
import cn.momia.api.base.http.MomiaHttpParamBuilder;
import cn.momia.api.base.http.MomiaHttpRequest;
import cn.momia.api.product.sku.Sku;
import cn.momia.api.product.topic.Topic;
import cn.momia.api.product.topic.Banner;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProductServiceApi extends ServiceApi {
    public static TopicServiceApi TOPIC = new TopicServiceApi();
    public static BaseProductServiceApi PRODUCT = new BaseProductServiceApi();
    public static SkuServiceApi SKU = new SkuServiceApi();
    public static FavoriteServiceApi FAVORITE = new FavoriteServiceApi();

    public void init() {
        TOPIC.setService(service);
        PRODUCT.setService(service);
        SKU.setService(service);
        FAVORITE.setService(service);
    }

    public static class TopicServiceApi extends ProductServiceApi {
        public List<Banner> listBanners(int cityId, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("city", cityId)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("topic/banner"), builder.build());
            JSONArray bannersJson = (JSONArray) executeRequest(request);

            List<Banner> banners = new ArrayList<Banner>();
            for (int i = 0; i < bannersJson.size(); i++) {
                JSONObject bannerJson = bannersJson.getJSONObject(i);
                banners.add(JSON.toJavaObject(bannerJson, Banner.class));
            }

            return banners;
        }

        public Topic get(long topicId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("topic", topicId));

            return JSON.toJavaObject((JSON) executeRequest(request), Topic.class);
        }
    }

    public static class BaseProductServiceApi extends ProductServiceApi {
        public List<Product> list(Collection<Long> productIds) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("pids", StringUtils.join(productIds, ","));
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/list"), builder.build());
            JSONArray productsJson = (JSONArray) executeRequest(request);

            List<Product> products = new ArrayList<Product>();
            for (int i = 0; i < productsJson.size(); i++) {
                JSONObject productJson = productsJson.getJSONObject(i);
                products.add(JSON.toJavaObject(productJson, Product.class));
            }

            return products;
        }

        public PagedProducts list(int cityId, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("city", cityId)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), PagedProducts.class);
        }

        public PagedProducts listByWeekend(int cityId, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("city", cityId)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/weekend"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), PagedProducts.class);
        }

        public List<ProductGroup> listByMonth(int cityId, int month) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("city", cityId)
                    .add("month", month);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/month"), builder.build());
            JSONArray productGroupsJson = (JSONArray) executeRequest(request);

            List<ProductGroup> productGroups = new ArrayList<ProductGroup>();
            for (int i = 0; i < productGroupsJson.size(); i++) {
                JSONObject productGroupJson = productGroupsJson.getJSONObject(i);
                productGroups.add(JSON.toJavaObject(productGroupJson, ProductGroup.class));
            }

            return productGroups;
        }

        public PagedProducts listNeedLeader(int cityId, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("city", cityId)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/leader"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), PagedProducts.class);
        }

        public Product get(long productId, int type) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("type", type);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", productId), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), Product.class);
        }

        public String getDetail(long productId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", productId, "detail"));

            return (String) executeRequest(request);
        }

        public void favor(long userId, long productId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("product", productId, "favor"), builder.build());
            executeRequest(request);
        }

        public void unfavor(long userId, long productId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("product", productId, "unfavor"), builder.build());
            executeRequest(request);
        }

        public boolean favored(long userId, long productId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", productId, "favored"), builder.build());

            return (Boolean) executeRequest(request);
        }

        public void sold(long productId, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("product", productId, "sold"), builder.build());
            executeRequest(request);
        }
    }

    public static class SkuServiceApi extends ProductServiceApi {
        public Sku get(long productId, long skuId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", productId, "sku", skuId));

            return JSON.toJavaObject((JSON) executeRequest(request), Sku.class);
        }

        public List<Sku> list(long productId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", productId, "sku"));
            JSONArray skusJson = (JSONArray) executeRequest(request);

            List<Sku> skus = new ArrayList<Sku>();
            for (int i = 0; i < skusJson.size(); i++) {
                JSONObject skuJson = skusJson.getJSONObject(i);
                skus.add(JSON.toJavaObject(skuJson, Sku.class));
            }

            return skus;
        }

        public List<Sku> listAll(long productId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", productId, "sku/all"));
            JSONArray skusJson = (JSONArray) executeRequest(request);

            List<Sku> skus = new ArrayList<Sku>();
            for (int i = 0; i < skusJson.size(); i++) {
                JSONObject skuJson = skusJson.getJSONObject(i);
                skus.add(JSON.toJavaObject(skuJson, Sku.class));
            }

            return skus;
        }

        public List<Sku> listWithLeader(long productId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", productId, "sku/leader"));
            JSONArray skusJson = (JSONArray) executeRequest(request);

            List<Sku> skus = new ArrayList<Sku>();
            for (int i = 0; i < skusJson.size(); i++) {
                JSONObject skuJson = skusJson.getJSONObject(i);
                skus.add(JSON.toJavaObject(skuJson, Sku.class));
            }

            return skus;
        }

        public void applyLeader(long userId, long productId, long skuId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("product", productId, "sku", skuId, "leader/apply"), builder.build());

            executeRequest(request);
        }

        public PagedProducts getLedProducts(long userId, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/led/list"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), PagedProducts.class);
        }

        public boolean lockStock(long productId, long skuId, int count, int joinedCount) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("count", count)
                    .add("joined", joinedCount);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("product", productId, "sku", skuId, "lock"), builder.build());

            return (Boolean) executeRequest(request);
        }

        public boolean unlockStock(long productId, long skuId, int count, int joinedCount) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("count", count)
                    .add("joined", joinedCount);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("product", productId, "sku", skuId, "unlock"), builder.build());

            return (Boolean) executeRequest(request);
        }
    }

    public static class FavoriteServiceApi extends ProductServiceApi {
        public PagedProducts listFavorites(long userId, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/favorite"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), PagedProducts.class);
        }
    }
}
