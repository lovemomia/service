package cn.momia.mapi.api.v1.dto;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;

public class SkuDto implements Dto {
    public static class Skus extends ArrayList<SkuDto> implements Dto {}

    private long productId;
    private long skuId;
    private int stock;
    private float minPrice;
    private String time;
    private JSONArray prices;

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public JSONArray getPrices() {
        return prices;
    }

    public void setPrices(JSONArray prices) {
        this.prices = prices;
    }
}
