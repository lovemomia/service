package cn.momia.service.base.product.sku;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;

public class SkuPrice implements Serializable {
    private static final String[] UNIT = { "人", "组" };

    private int adult;
    private int child;
    private BigDecimal price;
    private String unit;
    private String desc;

    public int getAdult() {
        return adult;
    }

    public int getChild() {
        return child;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }

    public String getDesc() {
        return desc;
    }

    public SkuPrice(JSONObject priceJson) {
        this.adult = priceJson.containsKey("adult") ? priceJson.getInteger("adult") : 0;
        this.child = priceJson.containsKey("child") ? priceJson.getInteger("child") : 0;
        this.price = priceJson.getBigDecimal("price");
        this.unit = UNIT[priceJson.getInteger("unit")];
        this.desc = priceJson.containsKey("desc") ? priceJson.getString("desc") : null;
    }
}
