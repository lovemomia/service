package cn.momia.service.base.product;

import java.util.List;

public interface ProductService {
    Product get(long id);
    List<Product> getByIds(List<Long> ids);
    List<Product> queryProducts(int start, int count, ProductQuery query);
}
