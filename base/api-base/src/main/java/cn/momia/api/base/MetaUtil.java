package cn.momia.api.base;

import cn.momia.api.base.dto.AgeRangeDto;
import cn.momia.api.base.dto.RegionDto;
import cn.momia.api.base.dto.CityDto;
import cn.momia.api.base.dto.SortTypeDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaUtil {
    private static AgeRangeServiceApi ageRangeServiceApi;
    private static CityServiceApi cityServiceApi;
    private static RegionServiceApi regionServiceApi;
    private static SortTypeServiceApi sortTypeServiceApi;

    private static Date lastReloadTime = null;

    private static List<AgeRangeDto> ageRangesCache = new ArrayList<AgeRangeDto>();
    private static List<SortTypeDto> sortTypesCache = new ArrayList<SortTypeDto>();

    private static Map<Integer, AgeRangeDto> ageRangesMap = new HashMap<Integer, AgeRangeDto>();
    private static Map<Integer, CityDto> citiesMap = new HashMap<Integer, CityDto>();
    private static Map<Integer, RegionDto> regionsMap = new HashMap<Integer, RegionDto>();
    private static Map<Integer, SortTypeDto> sortTypesMap = new HashMap<Integer, SortTypeDto>();

    public void setAgeRangeServiceApi(AgeRangeServiceApi ageRangeServiceApi) {
        MetaUtil.ageRangeServiceApi = ageRangeServiceApi;
    }

    public void setCityServiceApi(CityServiceApi cityServiceApi) {
        MetaUtil.cityServiceApi = cityServiceApi;
    }

    public void setRegionServiceApi(RegionServiceApi regionServiceApi) {
        MetaUtil.regionServiceApi = regionServiceApi;
    }

    public void setSortTypeServiceApi(SortTypeServiceApi sortTypeServiceApi) {
        MetaUtil.sortTypeServiceApi = sortTypeServiceApi;
    }

    private synchronized static void reload() {
        if (!isOutOfDate()) return;

        try {
            List<AgeRangeDto> newAgeRanges = ageRangeServiceApi.listAll();
            newAgeRanges.add(AgeRangeDto.DEFAULT);
            Map<Integer, AgeRangeDto> newAgeRangesMap = new HashMap<Integer, AgeRangeDto>();
            for (AgeRangeDto ageRange : newAgeRanges) {
                newAgeRangesMap.put(ageRange.getId(), ageRange);
            }

            Map<Integer, CityDto> newCitiesMap = new HashMap<Integer, CityDto>();
            for (CityDto city : cityServiceApi.listAll()) {
                newCitiesMap.put(city.getId(), city);
            }

            Map<Integer, RegionDto> newRegionsMap = new HashMap<Integer, RegionDto>();
            for (RegionDto region : regionServiceApi.listAll()) {
                newRegionsMap.put(region.getId(), region);
            }

            List<SortTypeDto> newSortTypes = sortTypeServiceApi.listAll();
            newSortTypes.add(SortTypeDto.DEFAULT);
            Map<Integer, SortTypeDto> newSortTypesMap = new HashMap<Integer, SortTypeDto>();
            for (SortTypeDto sortType : newSortTypes) {
                newSortTypesMap.put(sortType.getId(), sortType);
            }

            ageRangesCache = newAgeRanges;
            sortTypesCache = newSortTypes;

            ageRangesMap = newAgeRangesMap;
            citiesMap = newCitiesMap;
            regionsMap = newRegionsMap;
            sortTypesMap = newSortTypesMap;
        } catch (Exception e) {
            // do nothing
        } finally {
            lastReloadTime = new Date();
        }
    }

    private static boolean isOutOfDate() {
        return lastReloadTime == null || lastReloadTime.before(new Date(new Date().getTime() - 24 * 60 * 60 * 1000));
    }

    public static List<AgeRangeDto> listAgeRanges() {
        return ageRangesCache;
    }

    public static AgeRangeDto getAgeRange(int ageRangeId) {
        if (isOutOfDate()) reload();

        AgeRangeDto ageRange = ageRangesMap.get(ageRangeId);
        return ageRange == null ? AgeRangeDto.DEFAULT : ageRange;
    }

    public static String getCityName(int cityId) {
        if (isOutOfDate()) reload();

        CityDto city = citiesMap.get(cityId);
        return city == null ? "" : city.getName();
    }

    public static String getRegionName(int regionId) {
        if (isOutOfDate()) reload();

        if (regionId == RegionDto.MULTI_REGION_ID) return "多商区";

        RegionDto region = regionsMap.get(regionId);
        return region == null ? "" : region.getName();
    }

    public static List<SortTypeDto> listSortTypes() {
        return sortTypesCache;
    }

    public static SortTypeDto getSortType(int sortTypeId) {
        if (isOutOfDate()) reload();

        SortTypeDto sortType = sortTypesMap.get(sortTypeId);
        return sortType == null ? SortTypeDto.DEFAULT : sortType;
    }
}