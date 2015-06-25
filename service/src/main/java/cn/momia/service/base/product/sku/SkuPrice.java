package cn.momia.service.base.product.sku;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class SkuPrice implements Serializable {
    private static final String[] UNIT = { "人", "组" };

    private int adult;
    private int child;
    private float price;
    private String unit;

    public int getAdult() {
        return adult;
    }

    public int getChild() {
        return child;
    }

    public float getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }

    public SkuPrice(JSONObject priceJson) {
        this.adult = priceJson.containsKey("adult") ? priceJson.getInteger("adult") : 0;
        this.child = priceJson.containsKey("child") ? priceJson.getInteger("child") : 0;
        this.price = priceJson.getFloat("price");
        this.unit = UNIT[priceJson.getInteger("unit")];
    }
}
