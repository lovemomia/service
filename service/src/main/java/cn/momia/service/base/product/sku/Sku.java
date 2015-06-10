package cn.momia.service.base.product.sku;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Sku {
    public static final Sku NOT_EXIST_SKU = new Sku();

    static {
        NOT_EXIST_SKU.setId(0);
    }

    private long id;
    private float price;
    private int stock;
    private List<Pair<SkuProperty, SkuPropertyValue>> properties;

    public Sku() {

    }

    public Sku(JSONObject jsonObject) {
        setId(jsonObject.getInteger("id"));
        setPrice(jsonObject.getFloat("price"));
        setStock(jsonObject.getInteger("stock"));
        // TODO sku values
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<Pair<SkuProperty, SkuPropertyValue>> getProperties() {
        return properties;
    }

    public void setProperties(List<Pair<SkuProperty, SkuPropertyValue>> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sku)) return false;

        Sku sku = (Sku) o;

        return getId() == sku.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_SKU);
    }
}
