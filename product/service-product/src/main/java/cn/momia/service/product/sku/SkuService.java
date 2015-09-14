package cn.momia.service.product.sku;

import java.util.Collection;
import java.util.List;

public interface SkuService {
    Sku get(long id);
    List<Sku> queryByProduct(long productId);
    List<Sku> queryAllByProduct(long productId);
    List<Sku> queryByProducts(Collection<Long> productIds);
    boolean addLeader(long userId, long productId, long id);
    boolean lock(long id, int count);
    boolean unlock(long id, int count);

    long queryCountOfLedSkus(long userId);
    List<Sku> queryLedSkus(long userId, int start, int count);
}
