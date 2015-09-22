package cn.momia.api.base.dto;

import java.util.List;

public class CityDistrictsDto {
    private CityDto city;
    private List<RegionDto> districts;

    public CityDto getCity() {
        return city;
    }

    public void setCity(CityDto city) {
        this.city = city;
    }

    public List<RegionDto> getDistricts() {
        return districts;
    }

    public void setDistricts(List<RegionDto> districts) {
        this.districts = districts;
    }
}
