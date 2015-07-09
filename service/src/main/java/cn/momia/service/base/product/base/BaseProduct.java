package cn.momia.service.base.product.base;

import com.alibaba.fastjson.JSONArray;

import java.util.Date;

public class BaseProduct {
    public static final BaseProduct NOT_EXIST_BASEPRODUCT = new BaseProduct();
    public static final BaseProduct INVALID_BASEPRODUCT = new BaseProduct();

    static {
        NOT_EXIST_BASEPRODUCT.setId(0);
        INVALID_BASEPRODUCT.setId(0);
    }

    private long id;
    private int cityId;
    private String title;
    private String cover;
    private String crowd;
    private long placeId;
    private JSONArray content;
    private int sales;
    private Date startTime;
    private Date endTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCrowd() {
        return crowd;
    }

    public void setCrowd(String crowd) {
        this.crowd = crowd;
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public JSONArray getContent() {
        return content;
    }

    public void setContent(JSONArray content) {
        this.content = content;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseProduct)) return false;

        BaseProduct baseProduct = (BaseProduct) o;

        return getId() == baseProduct.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_BASEPRODUCT);
    }
}
