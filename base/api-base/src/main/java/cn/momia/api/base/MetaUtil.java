package cn.momia.api.base;

import cn.momia.api.base.dto.AgeRange;
import cn.momia.api.base.dto.Region;
import cn.momia.api.base.dto.City;
import cn.momia.api.base.dto.SortType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaUtil {
    private static MetaServiceApi metaServiceApi;

    private static Date lastReloadTime = null;

    private static List<AgeRange> ageRangesCache = new ArrayList<AgeRange>();
    private static List<SortType> sortTypesCache = new ArrayList<SortType>();

    private static Map<Integer, City> citiesMap = new HashMap<Integer, City>();
    private static Map<Integer, Region> regionsMap = new HashMap<Integer, Region>();
    private static Map<Integer, AgeRange> ageRangesMap = new HashMap<Integer, AgeRange>();
    private static Map<Integer, SortType> sortTypesMap = new HashMap<Integer, SortType>();

    public static void setMetaServiceApi(MetaServiceApi metaServiceApi) {
        MetaUtil.metaServiceApi = metaServiceApi;
    }

    private synchronized static void reload() {
        if (!isOutOfDate()) return;

        try {
            Map<Integer, City> newCitiesMap = new HashMap<Integer, City>();
            for (City city : metaServiceApi.listAllCities()) {
                newCitiesMap.put(city.getId(), city);
            }

            Map<Integer, Region> newRegionsMap = new HashMap<Integer, Region>();
            for (Region region : metaServiceApi.listAllRegions()) {
                newRegionsMap.put(region.getId(), region);
            }

            List<AgeRange> newAgeRanges = metaServiceApi.listAllAgeRanges();
            newAgeRanges.add(AgeRange.DEFAULT);
            Collections.sort(newAgeRanges, new Comparator<AgeRange>() {
                @Override
                public int compare(AgeRange ageRange1, AgeRange ageRange2) {
                    return ageRange1.getId() - ageRange2.getId();
                }
            });
            Map<Integer, AgeRange> newAgeRangesMap = new HashMap<Integer, AgeRange>();
            for (AgeRange ageRange : newAgeRanges) {
                newAgeRangesMap.put(ageRange.getId(), ageRange);
            }

            List<SortType> newSortTypes = metaServiceApi.listAllSortTypes();
            newSortTypes.add(SortType.DEFAULT);
            Collections.sort(newSortTypes, new Comparator<SortType>() {
                @Override
                public int compare(SortType sortType1, SortType sortType2) {
                    return sortType1.getId() - sortType2.getId();
                }
            });
            Map<Integer, SortType> newSortTypesMap = new HashMap<Integer, SortType>();
            for (SortType sortType : newSortTypes) {
                newSortTypesMap.put(sortType.getId(), sortType);
            }

            ageRangesCache = newAgeRanges;
            sortTypesCache = newSortTypes;

            citiesMap = newCitiesMap;
            regionsMap = newRegionsMap;
            ageRangesMap = newAgeRangesMap;
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

    public static List<AgeRange> listAgeRanges() {
        return ageRangesCache;
    }

    public static AgeRange getAgeRange(int ageRangeId) {
        if (isOutOfDate()) reload();

        AgeRange ageRange = ageRangesMap.get(ageRangeId);
        return ageRange == null ? AgeRange.DEFAULT : ageRange;
    }

    public static List<SortType> listSortTypes() {
        return sortTypesCache;
    }

    public static SortType getSortType(int sortTypeId) {
        if (isOutOfDate()) reload();

        SortType sortType = sortTypesMap.get(sortTypeId);
        return sortType == null ? SortType.DEFAULT : sortType;
    }
}