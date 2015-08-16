package cn.momia.service.common.api.region;

import cn.momia.service.common.api.city.City;

import java.util.List;

public class CityDistrict {
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

    @Override
    public String toString() {
        return "CityDistrict{" +
                "city=" + city +
                ", districts=" + districts +
                '}';
    }
}
