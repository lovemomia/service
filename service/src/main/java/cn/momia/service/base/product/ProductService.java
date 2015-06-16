package cn.momia.service.base.product;

import java.util.List;

public interface ProductService {
    Product get(long id);
    List<Product> queryProducts(int start, int count, ProductQuery query);
}
