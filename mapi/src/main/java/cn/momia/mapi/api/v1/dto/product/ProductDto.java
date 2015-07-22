package cn.momia.mapi.api.v1.dto.product;

import cn.momia.mapi.api.v1.dto.base.Dto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ProductDto implements Dto {
    // base info
    private long id;
    private String cover;
    private String thumb;
    private String title;
    private String abstracts;
    private int joined;
    private int sales;
    private boolean soldOut;
    private BigDecimal price;
    private String crowd;
    private String scheduler;
    private String address;
    private String poi;
    private JSONArray tags;
    @JSONField(format = "yyyy-MM-dd hh:mm:ss") private Date onlineTime;
    @JSONField(format = "yyyy-MM-dd hh:mm:ss") private Date offlineTime;
    private int status;
    private boolean opened = true;

    // extra info
    private List<String> imgs;
    private JSONArray content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public boolean isSoldOut() {
        return soldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCrowd() {
        return crowd;
    }

    public void setCrowd(String crowd) {
        this.crowd = crowd;
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
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

    public JSONArray getTags() {
        return tags;
    }

    public void setTags(JSONArray tags) {
        this.tags = tags;
    }

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Date getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(Date offlineTime) {
        this.offlineTime = offlineTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDto)) return false;

        ProductDto that = (ProductDto) o;

        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public ProductDto() {}

    public ProductDto(ProductDto productDto) {
        this.id = productDto.id;
        this.cover = productDto.cover;
        this.thumb = productDto.thumb;
        this.title = productDto.title;
        this.abstracts = productDto.abstracts;
        this.joined = productDto.joined;
        this.sales = productDto.sales;
        this.soldOut = productDto.soldOut;
        this.price = productDto.price;
        this.crowd = productDto.crowd;
        this.scheduler = productDto.scheduler;
        this.address = productDto.address;
        this.poi = productDto.poi;
        this.tags = productDto.tags;
        this.onlineTime = productDto.onlineTime;
        this.offlineTime = productDto.offlineTime;
        this.status = productDto.status;
        this.opened = productDto.opened;

        this.imgs = productDto.imgs;
        this.content = productDto.content;
    }
}
