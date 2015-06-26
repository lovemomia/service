package cn.momia.service.base.place;

import java.io.Serializable;
import java.util.List;

public class Place implements Serializable {
    public static final Place NOT_EXIST_PLACE = new Place();
    static {
        NOT_EXIST_PLACE.setId(0);
    }

    private long id;
    private String name;
    private String address;
    private String desc;
    private double lng;
    private double lat;
    private List<PlaceImage> imgs;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Place)) return false;

        Place place = (Place) o;

        return getId() == place.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_PLACE);
    }
}
