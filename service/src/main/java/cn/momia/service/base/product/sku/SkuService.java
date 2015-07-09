package cn.momia.service.base.product.sku;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SkuService {
    Sku get(long id);
    List<Sku> queryByProduct(long productId);
    Map<Long, List<Sku>> queryByProducts(Collection<Long> productIds);
    boolean lock(long id, int count);
    boolean unlock(long id, int count);
}
