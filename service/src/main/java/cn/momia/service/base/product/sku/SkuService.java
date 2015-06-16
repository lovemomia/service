package cn.momia.service.base.product.sku;

import java.util.List;

public interface SkuService {
    List<Sku> queryByProduct(long productId);
    boolean lock(long id, int count);
    boolean unlock(long id, int count);
}
