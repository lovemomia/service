package cn.momia.service.deal.order;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

public class OrderPrice {
    private BigDecimal price;
    private int count;
    private int adult;
    private int child;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

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

    public OrderPrice() {}

    public OrderPrice(JSONObject priceJson) {
        this.price = priceJson.getBigDecimal("price");
        this.count = priceJson.getInteger("count");
        this.adult = priceJson.containsKey("adult") ? priceJson.getInteger("adult") : 0;
        this.child = priceJson.containsKey("child") ? priceJson.getInteger("child") : 0;
    }
}
