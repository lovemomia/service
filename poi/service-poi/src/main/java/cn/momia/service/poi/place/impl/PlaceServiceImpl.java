package cn.momia.service.poi.place.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.poi.place.Place;
import cn.momia.service.poi.place.PlaceService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceServiceImpl extends AbstractService implements PlaceService {
    @Override
    public Place get(int placeId) {
        List<Place> places = list(Sets.newHashSet(placeId));
        return places.isEmpty() ? Place.NOT_EXIST_PLACE : places.get(0);
    }

    @Override
    public List<Place> list(Collection<Integer> placeIds) {
        String sql = "SELECT Id, CityId, RegionId, Name, Address, `Desc`, Cover, Lng, Lat, Route FROM SG_Place WHERE Id IN (%s) AND Status=1";
        List<Place> places = listByIds(sql, placeIds, Integer.class, Place.class);

        Map<Integer, List<String>> imgsMap = queryImgs(placeIds);
        for (Place place : places) {
            List<String> imgs = imgsMap.get(place.getId());
            place.setImgs(imgs != null ? imgs : new ArrayList<String>());
        }

        return places;
    }

    private Map<Integer, List<String>> queryImgs(Collection<Integer> placeIds) {
        if (placeIds.isEmpty()) return new HashMap<Integer, List<String>>();

        String sql = String.format("SELECT PlaceId, Url FROM SG_PlaceImg WHERE PlaceId IN (%s) AND Status=1 ORDER BY AddTime DESC", StringUtils.join(placeIds, ","));
        return queryListMap(sql, Integer.class, String.class);
    }
}
