package cn.momia.service.product.sku;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;

public class SkuPrice implements Serializable {
    private static final String[] UNIT = { "人", "组" };

    private int adult;
    private int child;
    private BigDecimal price;
    private BigDecimal origin;
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

    public BigDecimal getOrigin() {
        return origin;
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
        this.origin = priceJson.containsKey("origin") ? priceJson.getBigDecimal("origin") : new BigDecimal(0);
        this.unit = UNIT[priceJson.getInteger("unit")];
        this.desc = priceJson.containsKey("desc") ? priceJson.getString("desc") : null;
    }
}
