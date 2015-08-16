package cn.momia.service.product.api.topic;

import cn.momia.service.product.api.product.Product;

import java.util.List;

public class TopicGroup {
    private String title;
    private List<Product> products;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
