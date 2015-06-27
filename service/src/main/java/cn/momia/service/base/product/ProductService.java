package cn.momia.service.base.product;

import cn.momia.service.base.product.sku.Sku;

import java.util.List;

public interface ProductService {
    Product get(long id);
    List<Product> get(List<Long> ids);
    List<Product> query(int start, int count, ProductQuery query);
    List<Sku> getSkus(long id);
    boolean lockStock(long skuId, int count);
    boolean unlockStock(long skuId, int count);
}
