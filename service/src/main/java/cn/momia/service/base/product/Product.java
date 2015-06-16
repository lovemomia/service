package cn.momia.service.base.product;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class Product {
    public static final Product NOT_EXIST_PRODUCT = new Product();
    static {
        NOT_EXIST_PRODUCT.setId(0);
    }

    private long id;
    private int categoryId;
    private String title;
    private JSONObject content;
    private int sales;
    private List<ProductImage> imgs;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public List<ProductImage> getImgs() {
        return imgs;
    }

    public void setImgs(List<ProductImage> imgs) {
        this.imgs = imgs;
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

    public boolean exists() {
        return !this.equals(NOT_EXIST_PRODUCT);
    }
}
