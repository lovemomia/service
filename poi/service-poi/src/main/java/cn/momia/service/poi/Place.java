package cn.momia.service.poi;

import java.util.List;

public class Place {
    public static class Type {
        public static final int BASE = 1;
        public static final int FULL = 2;
    }

    public static final Place NOT_EXIST_PLACE = new Place();

    private int id;
    private int cityId;
    private int regionId;
    private String name;
    private String address;
    private String desc;
    private String cover;
    private double lng;
    private double lat;

    private List<PlaceImage> imgs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public List<PlaceImage> getImgs() {
        return imgs;
    }

    public void setImgs(List<PlaceImage> imgs) {
        this.imgs = imgs;
    }

    public boolean exists() {
        return id > 0;
    }
}
