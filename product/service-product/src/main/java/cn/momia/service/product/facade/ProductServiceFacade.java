package cn.momia.service.product.facade;

import cn.momia.service.product.base.ProductSort;
import cn.momia.service.product.sku.Sku;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProductServiceFacade {
    Product get(long productId);
    Product get(long productId, boolean mini);
    List<Product> list(Collection<Long> productIds);
    Map<Long, List<Product>> listGrouped(Map<Long, List<Long>> groupedProductIds);
    String getDetail(long productId);

    long queryCount(int cityId);
    List<Product> query(int cityId, int start, int count, ProductSort productSort);
    long queryCountByWeekend(int cityId);
    List<Product> queryByWeekend(int cityId, int start, int count);
    List<Product> queryByMonth(int cityId, int month);
    long queryCountNeedLeader(int cityId);
    List<Product> queryNeedLeader(int cityId, int start, int count);

    boolean sold(long productId, int count);

    List<Sku> listSkus(long productId);
    Sku getSku(long skuId);
    boolean lockStock(long productId, long skuId, int count, int joined);
    boolean unlockStock(long productId, long skuId, int count, int joined);

    boolean addSkuLeader(long userId, long productId, long skuId);

    long queryCountOfLedSkus(long userId);
    List<Sku> queryLedSkus(long userId, int start, int count);
}
