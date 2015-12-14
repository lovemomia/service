package cn.momia.service.poi;

import cn.momia.api.poi.dto.Place;

import java.util.Collection;
import java.util.List;

public interface PlaceService {
    Place get(int placeId);
    List<Place> list(Collection<Integer> placeIds);
}
