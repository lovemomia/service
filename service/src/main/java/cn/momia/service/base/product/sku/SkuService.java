package cn.momia.sku;

import java.util.List;

public interface SkuService {
    long add(Sku sku);
    boolean update(Sku sku);
    Sku get(long id);
    List<Sku> queryByProduct(long productId);
}
