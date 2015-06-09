package cn.momia.service.base.product;

public interface ProductService {
    long add(Product product);
    boolean update(Product product);
    Product get(long productId);
}
