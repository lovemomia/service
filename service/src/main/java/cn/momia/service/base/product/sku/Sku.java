package cn.momia.sku;

import cn.momia.product.Product;
import cn.momia.product.factory.ProductFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Sku {
    public static final Sku NOT_EXIST_SKU = new Sku();

    static {
        NOT_EXIST_SKU.setId(0);
    }

    private long id;
    private Product product;
    private List<Long> propertyValues;
    private float price;
    private int stock;

    public Sku() {

    }

    public Sku(JSONObject jsonObject) {
        setId(jsonObject.getInteger("id"));
        setProduct(ProductFactory.create(jsonObject.getJSONObject("product")));
        List<Long> propertyValues = new ArrayList<Long>();
        JSONArray propertyValuesArray = jsonObject.getJSONArray("propertyValues");
        for (int i = 0; i < propertyValuesArray.size(); i++) {
            propertyValues.add(propertyValuesArray.getLong(i));
        }
        setPropertyValues(propertyValues);
        setPrice(jsonObject.getFloat("price"));
        setStock(jsonObject.getInteger("stock"));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Long> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(List<Long> propertyValues) {
        this.propertyValues = propertyValues;
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
