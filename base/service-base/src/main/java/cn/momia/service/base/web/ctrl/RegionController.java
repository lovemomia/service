package cn.momia.service.base.web.ctrl;

import cn.momia.api.base.dto.CityDistrictsDto;
import cn.momia.api.base.dto.CityDto;
import cn.momia.api.base.dto.RegionDto;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.base.city.City;
import cn.momia.service.base.city.CityService;
import cn.momia.service.base.region.Region;
import cn.momia.service.base.region.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/region")
public class RegionController extends BaseController {
    @Autowired private CityService cityService;
    @Autowired private RegionService regionService;

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse listAll() {
        return MomiaHttpResponse.SUCCESS(regionService.listAll());
    }

    @RequestMapping(value = "/district/tree", method = RequestMethod.GET)
    public MomiaHttpResponse getDistrictTree() {
        List<City> cities = cityService.listAll();
        List<Region> regions = regionService.listAll();
        Map<Integer, List<Region>> districtsOfCities = new HashMap<Integer, List<Region>>();
        for (Region region : regions) {
            if (region.getParentId() > 0) continue;

            int cityId = region.getCityId();
            List<Region> districts = districtsOfCities.get(cityId);
            if (districts == null) {
                districts = new ArrayList<Region>();
                districtsOfCities.put(cityId, districts);
            }
            districts.add(region);
        }

        List<CityDistrictsDto> cityDistrictsDtos = new ArrayList<CityDistrictsDto>();
        for (City city : cities) {
            cityDistrictsDtos.add(buildCityDistrictsDto(city, districtsOfCities.get(city.getId())));
        }

        return MomiaHttpResponse.SUCCESS(cityDistrictsDtos);
    }

    private CityDistrictsDto buildCityDistrictsDto(City city, List<Region> districts) {
        CityDistrictsDto cityDistrictsDto = new CityDistrictsDto();
        cityDistrictsDto.setCity(buildCityDto(city));
        cityDistrictsDto.setDistricts(buildRegionDtos(districts));

        return cityDistrictsDto;
    }

    private CityDto buildCityDto(City city) {
        CityDto cityDto = new CityDto();
        cityDto.setId(city.getId());
        cityDto.setName(city.getName());

        return cityDto;
    }

    private List<RegionDto> buildRegionDtos(List<Region> regions) {
        List<RegionDto> districtDtos = new ArrayList<RegionDto>();
        if (regions != null) {
            for (Region region : regions) {
                districtDtos.add(buildRegionDto(region));
            }
        }

        return districtDtos;
    }

    private RegionDto buildRegionDto(Region region) {
        RegionDto regionDto = new RegionDto();
        regionDto.setId(region.getId());
        regionDto.setCityId(region.getCityId());
        regionDto.setName(region.getName());
        regionDto.setParentId(region.getParentId());

        return regionDto;
    }
}
