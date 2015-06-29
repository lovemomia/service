package cn.momia.service.base.product;

import cn.momia.service.base.product.base.BaseProduct;
import cn.momia.service.base.product.place.Place;
import cn.momia.service.base.product.sku.Sku;
import com.alibaba.fastjson.JSONArray;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    public static final Product NOT_EXIST_PRODUCT = new Product();

    private BaseProduct baseProduct;
    private List<ProductImage> imgs;
    private Place place;
    private List<Sku> skus;

    public long getId() {
        if (baseProduct == null || !baseProduct.exists()) return 0;
        return baseProduct.getId();
    }

    public int getCityId() {
        return baseProduct.getCityId();
    }

    public String getTitle() {
        return baseProduct.getTitle();
    }

    public String getCover() {
        return baseProduct.getCover();
    }

    public String getCrowd() {
        return baseProduct.getCrowd();
    }

    public JSONArray getContent() {
        return baseProduct.getContent();
    }

    public int getSales() {
        return baseProduct.getSales();
    }

    public void setBaseProduct(BaseProduct baseProduct) {
        this.baseProduct = baseProduct;
    }

    public List<ProductImage> getImgs() {
        return imgs;
    }

    public void setImgs(List<ProductImage> imgs) {
        this.imgs = imgs;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
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

    public boolean exists() {
        return !this.equals(NOT_EXIST_PRODUCT);
    }
}
