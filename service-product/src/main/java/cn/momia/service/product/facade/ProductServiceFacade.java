package cn.momia.service.product.facade;

import cn.momia.service.product.sku.Sku;

import java.util.Collection;
import java.util.List;

public interface ProductServiceFacade {
    Product get(long id);
    Product get(long id, boolean mini);
    List<Product> get(Collection<Long> ids);

    long queryCount(int cityId);
    List<Product> query(int cityId, int start, int count);
    long queryCountByWeekend(int cityId);
    List<Product> queryByWeekend(int cityId, int start, int count);
    long queryCountByMonth(int cityId, int month);
    List<Product> queryByMonth(int cityId, int month);

    List<Sku> getSkus(long id);
    Sku getSku(long skuId);
    List<Sku> getSkusWithoutLeader(long productId);
    boolean lockStock(long id, long skuId, int count);
    boolean unlockStock(long id, long skuId, int count);
    boolean sold(long id, int count);

    boolean isFavoried(long userId, long id);
    boolean favor(long userId, long id);
    boolean unFavor(long userId, long id);

    long queryFavoritesCount(long userId);
    List<Product> queryFavorites(long userId, int start, int count);
}
