package cn.momia.service.product.facade;

import cn.momia.service.product.banner.Banner;
import cn.momia.service.product.base.ProductSort;
import cn.momia.service.product.sku.Sku;
import cn.momia.service.product.topic.Topic;
import cn.momia.service.product.topic.TopicGroup;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProductServiceFacade {
    List<Banner> getBanners(int cityId, int count);

    Product get(long productId);
    Product get(long productId, boolean mini);
    List<Product> get(Collection<Long> productIds);
    String getDetail(long productId);

    long queryCount(int cityId);
    List<Product> query(int cityId, int start, int count, ProductSort productSort);
    long queryCountByWeekend(int cityId);
    List<Product> queryByWeekend(int cityId, int start, int count);
    long queryCountByMonth(int cityId, int month);
    List<Product> queryByMonth(int cityId, int month);
    long queryCountNeedLeader(int cityId);
    List<Product> queryNeedLeader(int cityId, int start, int count);

    long queryCountOfLedSkus(long userId);
    List<Sku> queryLedSkus(long userId, int start, int count);

    List<Sku> getSkus(long productId);
    List<Sku> getAllSkus(long productId);
    Sku getSku(long skuId);
    List<Sku> getSkusWithoutLeader(long productId);
    boolean addSkuLeader(long userId, long productId, long skuId);
    boolean lockStock(long productId, long skuId, int count, int joined);
    boolean unlockStock(long productId, long skuId, int count, int joined);
    boolean sold(long productId, int count);

    boolean isFavoried(long userId, long productId);
    boolean favor(long userId, long productId);
    boolean unFavor(long userId, long productId);

    long queryFavoritesCount(long userId);
    List<Product> queryFavorites(long userId, int start, int count);

    Topic getTopic(long topicId);
    List<TopicGroup> getTopicGroups(long topicId);
    Map<Long,List<Product>> queryByTopicGroups(List<Long> groupIds);
}
