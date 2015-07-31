package cn.momia.service.product.sku;

import cn.momia.common.service.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SkuService extends Service {
    Sku get(long id);
    List<Sku> queryByProduct(long productId);
    Map<Long, List<Sku>> queryByProducts(Collection<Long> productIds);
    boolean addLeader(long userId, long productId, long id);
    boolean lock(long id, int count);
    boolean unlock(long id, int count);

    long queryCountOfLedSkus(long userId);
    List<Sku> queryLedSkus(long userId, int start, int count);
}
