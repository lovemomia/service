package cn.momia.service.base.product;

import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.user.User;

import java.util.List;

public interface ProductService {
    long add(Product product);
    boolean update(Product product);
    Product get(long productId);
    List<Sku> getSkus(long productId);
}
