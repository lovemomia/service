package cn.momia.api.product.dto;

import java.util.List;

public class TopicGroupDto {
    private String title;
    private List<ProductDto> products;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }
}
