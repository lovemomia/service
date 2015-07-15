package cn.momia.service.base.product;

import cn.momia.service.base.product.sku.Sku;

import java.util.Collection;
import java.util.List;

public interface ProductService {
    Product get(long id);
    List<Product> get(Collection<Long> ids);
    long queryCount(ProductQuery productQuery);
    List<Product> query(int start, int count, ProductQuery query);
    List<Sku> getSkus(long id);
    Sku getSku(long skuId);
    boolean lockStock(long id, long skuId, int count);
    boolean unlockStock(long id, long skuId, int count);
    boolean join(long productId, int count);
    boolean sold(long id, int count);
}
