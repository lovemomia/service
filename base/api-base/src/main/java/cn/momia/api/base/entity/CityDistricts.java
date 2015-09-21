package cn.momia.api.base.entity;

import java.util.List;

public class CityDistricts {
    private City city;
    private List<Region> districts;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<Region> getDistricts() {
        return districts;
    }

    public void setDistricts(List<Region> districts) {
        this.districts = districts;
    }
}
