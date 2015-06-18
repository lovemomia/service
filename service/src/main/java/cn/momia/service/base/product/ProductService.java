package cn.momia.service.base.product;

import java.util.List;

public interface ProductService {
    Product get(long id);
    List<Product> get(List<Long> ids);
    List<Product> query(int start, int count, ProductQuery query);
}
