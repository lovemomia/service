package cn.momia.api.product;

import cn.momia.api.product.entity.Comment;
import cn.momia.api.product.entity.Product;
import cn.momia.api.product.entity.ProductGroup;
import cn.momia.api.product.entity.Sku;
import cn.momia.api.product.entity.Topic;
import cn.momia.api.product.entity.Banner;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.entity.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.http.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;

public class ProductServiceApi extends AbstractServiceApi {
    public static TopicServiceApi TOPIC = new TopicServiceApi();
    public static BaseProductServiceApi PRODUCT = new BaseProductServiceApi();
    public static CommentServiceApi COMMENT = new CommentServiceApi();
    public static SkuServiceApi SKU = new SkuServiceApi();
    public static FavoriteServiceApi FAVORITE = new FavoriteServiceApi();

    public void init() {
        TOPIC.setService(service);
        PRODUCT.setService(service);
        COMMENT.setService(service);
        SKU.setService(service);
        FAVORITE.setService(service);
    }

    public static class TopicServiceApi extends ProductServiceApi {
        public List<Banner> listBanners(int cityId, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("city", cityId)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("topic/banner"), builder.build());

            return CastUtil.toList((JSONArray) executeRequest(request), Banner.class);
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

            return CastUtil.toList((JSONArray) executeRequest(request), Product.class);
        }

        public PagedList<Product> list(int cityId, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("city", cityId)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product"), builder.build());

            return CastUtil.toPagedList((JSONObject) executeRequest(request), Product.class);
        }

        public PagedList<Product> listByWeekend(int cityId, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("city", cityId)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/weekend"), builder.build());

            return CastUtil.toPagedList((JSONObject) executeRequest(request), Product.class);
        }

        public List<ProductGroup> listByMonth(int cityId, int month) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("city", cityId)
                    .add("month", month);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/month"), builder.build());

            return CastUtil.toList((JSONArray) executeRequest(request), ProductGroup.class);
        }

        public PagedList<Product> listNeedLeader(int cityId, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("city", cityId)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/leader"), builder.build());

            return CastUtil.toPagedList((JSONObject) executeRequest(request), Product.class);
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

    public static class CommentServiceApi extends ProductServiceApi {
        public void add(JSONObject commentJson) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("product", commentJson.getLong("productId"), "comment"), commentJson.toJSONString());
            executeRequest(request);
        }

        public PagedList<Comment> list(long productId, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", productId, "comment"), builder.build());

            return CastUtil.toPagedList((JSONObject) executeRequest(request), Comment.class);
        }
    }

    public static class SkuServiceApi extends ProductServiceApi {
        public Sku get(long productId, long skuId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", productId, "sku", skuId));
            return JSON.toJavaObject((JSON) executeRequest(request), Sku.class);
        }

        public List<Sku> list(long productId, int status) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("status", status);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", productId, "sku"), builder.build());

            return CastUtil.toList((JSONArray) executeRequest(request), Sku.class);
        }

        public List<Sku> listWithLeader(long productId) {
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product", productId, "sku/leader"));
            return CastUtil.toList((JSONArray) executeRequest(request), Sku.class);
        }

        public void applyLeader(long userId, long productId, long skuId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("product", productId, "sku", skuId, "leader/apply"), builder.build());
            executeRequest(request);
        }

        public PagedList<Product> getLedProducts(long userId, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/led/list"), builder.build());

            return CastUtil.toPagedList((JSONObject) executeRequest(request), Product.class);
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
        public PagedList<Product> listFavorites(long userId, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("product/favorite"), builder.build());

            return CastUtil.toPagedList((JSONObject) executeRequest(request), Product.class);
        }
    }
}
