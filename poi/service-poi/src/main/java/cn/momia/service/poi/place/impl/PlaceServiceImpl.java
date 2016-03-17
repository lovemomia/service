package cn.momia.service.poi.place.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.poi.place.Place;
import cn.momia.service.poi.place.PlaceService;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;

public class PlaceServiceImpl extends AbstractService implements PlaceService {
    @Override
    public Place get(int placeId) {
        List<Place> places = list(Sets.newHashSet(placeId));
        return places.isEmpty() ? Place.NOT_EXIST_PLACE : places.get(0);
    }

    @Override
    public List<Place> list(Collection<Integer> placeIds) {
        String sql = "SELECT Id, CityId, RegionId, Name, Address, `Desc`, Lng, Lat, Route FROM SG_Place WHERE Id IN (%s) AND Status=1";
        return listByIds(sql, placeIds, Integer.class, Place.class);
    }
}
