package cn.momia.api.product.dto;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

public class ProductDto {
    public static class Type {
        public static final int MINI = 1;
        public static final int BASE = 2;
        public static final int BASE_WITH_SKU = 3;
        public static final int FULL = 4;
    }

    private long id;
    private String thumb;
    private String title;
    private String abstracts;

    private String cover;
    private Integer joined;
    private String scheduler;
    private List<String> tags;
    private Integer regionId;
    private String region;
    private String address;
    private String poi;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Boolean soldOut;
    private Boolean opened;
    private String crowd;
    private Integer stock;
    private List<SkuDto> skus;
    private Integer status;

    private List<String> imgs;
    private JSONArray content;

    private String url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getJoined() {
        return joined;
    }

    public void setJoined(Integer joined) {
        this.joined = joined;
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Boolean isSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public Boolean isOpened() {
        return opened;
    }

    public void setOpened(Boolean opened) {
        this.opened = opened;
    }

    public String getCrowd() {
        return crowd;
    }

    public void setCrowd(String crowd) {
        this.crowd = crowd;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public List<SkuDto> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuDto> skus) {
        this.skus = skus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public JSONArray getContent() {
        return content;
    }

    public void setContent(JSONArray content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SkuDto getSku(long skuId) {
        for (SkuDto sku : skus) {
            if (sku.getSkuId() == skuId) return sku;
        }

        return SkuDto.NOT_EXIST_SKU;
    }

    public int getSkuRegionId(long skuId) {
        SkuDto sku = getSku(skuId);
        if (!sku.exists() || sku.getRegionId() == 0) return getRegionId();
        return sku.getRegionId();
    }

    public String getSkuAddress(long skuId) {
        SkuDto sku = getSku(skuId);
        if (!sku.exists() || StringUtils.isBlank(sku.getAddress())) return getAddress();
        return sku.getAddress();
    }

    public String getSkuTime(long skuId) {
        for (SkuDto sku : skus) {
            if (sku.getSkuId() == skuId) return sku.getTime();
        }

        return "";
    }

    public boolean isSkuFinished(long skuId) {
        SkuDto sku = getSku(skuId);
        if (!sku.exists()) return true;
        return sku.isFinished();
    }

    public boolean isSkuClosed(long skuId) {
        SkuDto sku = getSku(skuId);
        if (!sku.exists()) return true;
        return sku.isClosed();
    }

    public boolean exists() {
        return id > 0;
    }
}
