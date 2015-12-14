package cn.momia.service.poi.impl;

import cn.momia.api.poi.dto.Place;
import cn.momia.common.service.AbstractService;
import cn.momia.service.poi.PlaceService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlaceServiceImpl extends AbstractService implements PlaceService {
    @Override
    public Place get(int placeId) {
        Set<Integer> placeIds = Sets.newHashSet(placeId);
        List<Place> places = list(placeIds);

        return places.isEmpty() ? Place.NOT_EXIST_PLACE : places.get(0);
    }

    @Override
    public List<Place> list(Collection<Integer> placeIds) {
        if (placeIds.isEmpty()) return new ArrayList<Place>();

        String sql = "SELECT Id, CityId, RegionId, Name, Address, `Desc`, Cover, Lng, Lat, Route FROM SG_Place WHERE Id IN (" + StringUtils.join(placeIds, ",") + ") AND Status<>0";
        List<Place> places = queryObjectList(sql, Place.class);

        Map<Integer, List<String>> imgsMap = queryImgs(placeIds);
        for (Place place : places) {
            place.setImgs(imgsMap.get(place.getId()));
        }

        return places;
    }

    private Map<Integer, List<String>> queryImgs(Collection<Integer> placeIds) {
        if (placeIds.isEmpty()) return new HashMap<Integer, List<String>>();

        String sql = "SELECT PlaceId, Url FROM SG_PlaceImg WHERE PlaceId IN (" + StringUtils.join(placeIds, ",") + ") AND Status<>0 ORDER BY AddTime DESC";
        Map<Integer, List<String>> imgsMap = queryListMap(sql, Integer.class, String.class);

        for (int placeId : placeIds) {
            if (!imgsMap.containsKey(placeId)) imgsMap.put(placeId, new ArrayList<String>());
        }

        return imgsMap;
    }
}
