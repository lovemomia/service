package cn.momia.api.product.dto;

import java.math.BigDecimal;
import java.util.List;

public class SkuDto {
    public static class Status {
        public static final int ALL = 1;
        public static final int AVALIABLE = 2;
    }

    public static final SkuDto NOT_EXIST_SKU = new SkuDto();
    static {
        NOT_EXIST_SKU.setSkuId(0);
    }

    private long productId;
    private long skuId;
    private String desc;
    private int type;
    private int limit;
    private int stock;
    private BigDecimal minPrice;
    private BigDecimal minOriginalPrice;
    private String time;
    private int placeId;
    private String placeName;
    private int regionId;
    private String address;
    private boolean needLeader;
    private boolean hasLeader;
    private long leaderUserId;
    private String leaderInfo;
    private boolean full;
    private boolean finished;
    private boolean closed;

    private Boolean needRealName;
    private List<SkuPriceDto> prices;

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

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isNeedLeader() {
        return needLeader;
    }

    public void setNeedLeader(boolean needLeader) {
        this.needLeader = needLeader;
    }

    public boolean isHasLeader() {
        return hasLeader;
    }

    public void setHasLeader(boolean hasLeader) {
        this.hasLeader = hasLeader;
    }

    public long getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(long leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public String getLeaderInfo() {
        return leaderInfo;
    }

    public void setLeaderInfo(String leaderInfo) {
        this.leaderInfo = leaderInfo;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
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

    public List<SkuPriceDto> getPrices() {
        return prices;
    }

    public void setPrices(List<SkuPriceDto> prices) {
        this.prices = prices;
    }

    public boolean findPrice(int adult, int child, BigDecimal price) {
        for (SkuPriceDto skuPrice : this.prices) {
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
