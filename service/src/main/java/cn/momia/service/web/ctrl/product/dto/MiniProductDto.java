package cn.momia.service.web.ctrl.product.dto;

import cn.momia.service.product.Product;

public class MiniProductDto {
    protected Product product;

    public long getId() {
        return product.getId();
    }

    public String getThumb() {
        return product.getThumb();
    }

    public String getTitle() {
        return product.getTitle();
    }

    public String getAbstracts() {
        return product.getAbstracts();
    }

    public MiniProductDto(Product product) {
        this.product = product;
    }
}
