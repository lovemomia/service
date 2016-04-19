package cn.momia.api.poi;

import cn.momia.api.poi.dto.City;
import cn.momia.api.poi.dto.Region;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PoiUtil {
    private static PoiServiceApi poiServiceApi;

    private static Date lastReloadTime = null;

    private static Map<Integer, City> citiesMap = new HashMap<Integer, City>();
    private static Map<Integer, Region> regionsMap = new HashMap<Integer, Region>();

    public void setPoiServiceApi(PoiServiceApi poiServiceApi) {
        PoiUtil.poiServiceApi = poiServiceApi;
    }

    private synchronized static void reload() {
        if (!isOutOfDate()) return;

        try {
            Map<Integer, City> newCitiesMap = new HashMap<Integer, City>();
            for (City city : poiServiceApi.listAllCities()) {
                newCitiesMap.put(city.getId(), city);
            }

            Map<Integer, Region> newRegionsMap = new HashMap<Integer, Region>();
            for (Region region : poiServiceApi.listAllRegions()) {
                newRegionsMap.put(region.getId(), region);
            }

            citiesMap = newCitiesMap;
            regionsMap = newRegionsMap;
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

        City city = citiesMap.get(cityId);
        return city == null ? "" : city.getName();
    }

    public static String getRegionName(int regionId) {
        if (isOutOfDate()) reload();

        if (regionId == Region.MULTI_REGION_ID) return "多商区";

        Region region = regionsMap.get(regionId);
        return region == null ? "" : region.getName();
    }
}