package cn.momia.service.base.product;

import cn.momia.service.base.product.sku.Sku;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Product {
    public static class Status {

    }

    public static class Type {
        public static final int ACTIVITY = 1;
    }

    public static final Product NOT_EXIST_PRODUCT = new Product();

    static {
        NOT_EXIST_PRODUCT.setId(0);
    }

    private long id;
    private int category;
    private long userId;
    private String title;
    private String content;
    private int sales;
    private List<ProductImage> imgs;
    private List<Sku> skus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ProductImage> getImgs() {
        return imgs;
    }

    public void setImgs(List<ProductImage> imgs) {
        this.imgs = imgs;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        return getId() == product.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public Product() {

    }

    public Product(JSONObject jsonObject) {
        // TODO
        setId(jsonObject.getInteger("id"));
        List<Sku> skus = new ArrayList<Sku>();
        JSONArray propertyValuesArray = jsonObject.getJSONArray("skus");
        for (int i = 0; i < propertyValuesArray.size(); i++) {
            skus.add(propertyValuesArray.getObject(i,Sku.class));
        }
        setSkus(skus);
        setCategory(jsonObject.getInteger("category"));
        setTitle(jsonObject.getString("title"));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_PRODUCT);
    }
}
