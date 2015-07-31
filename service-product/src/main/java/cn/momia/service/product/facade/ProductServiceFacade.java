package cn.momia.service.product.facade;

import cn.momia.service.product.sku.Sku;

import java.util.Collection;
import java.util.List;

public interface ProductServiceFacade {
    Product get(long productId);
    Product get(long productId, boolean mini);
    List<Product> get(Collection<Long> productIds);

    long queryCount(int cityId);
    List<Product> query(int cityId, int start, int count);
    long queryCountByWeekend(int cityId);
    List<Product> queryByWeekend(int cityId, int start, int count);
    long queryCountByMonth(int cityId, int month);
    List<Product> queryByMonth(int cityId, int month);
    long queryCountNeedLeader(int cityId);
    List<Product> queryNeedLeader(int cityId, int start, int count);

    long queryCountOfLedSkus(long userId);
    List<Sku> queryLedSkus(long userId, int start, int count);

    List<Sku> getSkus(long productId);
    Sku getSku(long skuId);
    List<Sku> getSkusWithoutLeader(long productId);
    boolean addSkuLeader(long userId, long productId, long skuId);
    boolean lockStock(long productId, long skuId, int count);
    boolean unlockStock(long productId, long skuId, int count);
    boolean sold(long productId, int count);

    boolean isFavoried(long userId, long productId);
    boolean favor(long userId, long productId);
    boolean unFavor(long userId, long productId);

    long queryFavoritesCount(long userId);
    List<Product> queryFavorites(long userId, int start, int count);
}
