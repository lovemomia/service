package cn.momia.mapi.api.v1.dto.product;

import cn.momia.common.web.img.ImageFile;

import java.math.BigDecimal;

public class BaseProductDto extends MiniProductDto {
    private String cover;
    private int joined;
    private String scheduler;
    private String region;
    private String address;
    private String poi;
    private BigDecimal price;
    private boolean soldOut;
    private boolean opened = true;

    public String getCover() {
        return ImageFile.url(cover);
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
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

    public boolean isSoldOut() {
        return soldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }
}
