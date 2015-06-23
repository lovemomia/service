package cn.momia.service.base.product.sku;

import java.io.Serializable;

public class SkuPrice implements Serializable {
    private String name;
    private float price;

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public SkuPrice(String name, float price) {
        this.name = name;
        this.price = price;
    }
}
