package cn.momia.product;

public interface ProductService {
    long add(Product product);
    boolean update(Product product);
    Product get(long productId);
}
