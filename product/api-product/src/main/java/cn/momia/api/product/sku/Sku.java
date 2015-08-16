package cn.momia.api.product.sku;

import java.math.BigDecimal;
import java.util.List;

public class Sku {
    private long productId;
    private long skuId;
    private String desc;
    private int type;
    private int limit;
    private int stock;
    private BigDecimal minPrice;
    private BigDecimal minOriginalPrice;
    private String time;
    private boolean hasLeader;
    private String leaderInfo;
    private boolean closed;

    private Boolean needRealName;
    private List<SkuPrice> prices;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMinOriginalPrice() {
        return minOriginalPrice;
    }

    public void setMinOriginalPrice(BigDecimal minOriginalPrice) {
        this.minOriginalPrice = minOriginalPrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isHasLeader() {
        return hasLeader;
    }

    public void setHasLeader(boolean hasLeader) {
        this.hasLeader = hasLeader;
    }

    public String getLeaderInfo() {
        return leaderInfo;
    }

    public void setLeaderInfo(String leaderInfo) {
        this.leaderInfo = leaderInfo;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Boolean isNeedRealName() {
        return needRealName;
    }

    public void setNeedRealName(Boolean needRealName) {
        this.needRealName = needRealName;
    }

    public List<SkuPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<SkuPrice> prices) {
        this.prices = prices;
    }

    public boolean findPrice(int adult, int child, BigDecimal price) {
        for (SkuPrice skuPrice : this.prices) {
            if (skuPrice.getAdult() == adult &&
                    skuPrice.getChild() == child &&
                    skuPrice.getPrice().compareTo(price) == 0) return true;
        }

        return false;
    }

    public boolean exists() {
        return skuId > 0;
    }
}
