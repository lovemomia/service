package cn.momia.service.product.api.sku;

import java.math.BigDecimal;

public class SkuPrice {
    private int adult;
    private int child;
    private BigDecimal price;
    private BigDecimal origin;
    private String unit;
    private String desc;

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOrigin() {
        return origin;
    }

    public void setOrigin(BigDecimal origin) {
        this.origin = origin;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
