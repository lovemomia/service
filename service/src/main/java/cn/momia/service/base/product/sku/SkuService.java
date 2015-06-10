package cn.momia.service.base.product.sku;

import java.util.List;

public interface SkuService {
    long add(Sku sku);
    boolean update(Sku sku);
    Sku get(long id);
    List<Sku> queryByProduct(long productId);
    boolean lock(long skuId, int count);
    boolean unlock(long skuId, int count);
}
