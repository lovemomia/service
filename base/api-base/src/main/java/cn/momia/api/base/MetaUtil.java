package cn.momia.api.base;

import cn.momia.api.base.dto.RegionDto;
import cn.momia.api.base.dto.CityDto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MetaUtil {
    private static CityServiceApi cityServiceApi;
    private static RegionServiceApi regionServiceApi;

    private static Date lastReloadTime = null;

    private static Map<Integer, CityDto> citiesMap = new HashMap<Integer, CityDto>();
    private static Map<Integer, RegionDto> regionsMap = new HashMap<Integer, RegionDto>();

    public void setCityServiceApi(CityServiceApi cityServiceApi) {
        MetaUtil.cityServiceApi = cityServiceApi;
    }

    public void setRegionServiceApi(RegionServiceApi regionServiceApi) {
        MetaUtil.regionServiceApi = regionServiceApi;
    }

    private synchronized static void reload() {
        if (!isOutOfDate()) return;

        try {
            citiesMap = new HashMap<Integer, CityDto>();
            for (CityDto city : cityServiceApi.listAll()) citiesMap.put(city.getId(), city);

            regionsMap = new HashMap<Integer, RegionDto>();
            for (RegionDto region : regionServiceApi.listAll()) regionsMap.put(region.getId(), region);
        } catch (Exception e) {
            // do nothing
        } finally {
            lastReloadTime = new Date();
        }
    }

    private static boolean isOutOfDate() {
        return lastReloadTime == null || lastReloadTime.before(new Date(new Date().getTime() - 24 * 60 * 60 * 1000));
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
}