package cn.momia.service.base.product;

import cn.momia.service.base.product.sku.Sku;
import com.alibaba.fastjson.JSONObject;

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
    private String title;
    private List<ProductImage> imgs;
    private JSONObject content;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_PRODUCT);
    }
}
