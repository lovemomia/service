package cn.momia.service.base.product.sku;

import java.util.List;
import java.util.Map;

public interface SkuService {
    List<Sku> queryByProduct(long productId);
    Map<Long, List<Sku>> queryByProducts(List<Long> productIds);
    boolean lock(long id, int count);
    boolean unlock(long id, int count);
}
