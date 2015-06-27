package cn.momia.service.base.product;

import cn.momia.service.base.product.base.BaseProduct;
import cn.momia.service.base.product.place.Place;
import cn.momia.service.base.product.sku.Sku;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    public static final Product NOT_EXIST_PRODUCT = new Product();
    public static final Product INVALID_PRODUCT = new Product();

    private BaseProduct baseProduct;
    private List<ProductImage> imgs;
    private Place place;
    private List<Sku> skus;

    public BaseProduct getBaseProduct() {
        return baseProduct;
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

    public boolean exists() {
        return this != NOT_EXIST_PRODUCT && this != INVALID_PRODUCT;
    }
}
