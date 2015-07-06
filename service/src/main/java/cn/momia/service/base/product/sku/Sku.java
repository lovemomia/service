package cn.momia.service.base.product.sku;

import java.io.Serializable;
import java.util.List;

public class Sku implements Serializable {
    public static final Sku NOT_EXIST_SKU = new Sku();
    public static final Sku INVALID_SKU = new Sku();

    static {
        NOT_EXIST_SKU.setId(0);
        INVALID_SKU.setId(0);
    }

    private long id;
    private long productId;
    private List<SkuProperty> properties;
    private List<SkuPrice> prices;
    private int limit;
    private int stock;
    private int lockedStock;
    private int unlockedStock;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public List<SkuProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SkuProperty> properties) {
        this.properties = properties;
    }

    public List<SkuPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<SkuPrice> prices) {
        this.prices = prices;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getLockedStock() {
        return lockedStock;
    }

    public void setLockedStock(int lockedStock) {
        this.lockedStock = lockedStock;
    }

    public int getUnlockedStock() {
        return unlockedStock;
    }

    public void setUnlockedStock(int unlockedStock) {
        this.unlockedStock = unlockedStock;
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
