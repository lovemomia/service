package cn.momia.service.poi.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.poi.Place;
import cn.momia.service.poi.PlaceImage;
import cn.momia.service.poi.PlaceService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlaceServiceImpl extends DbAccessService implements PlaceService {
    @Override
    public Place get(int id) {
        Set<Integer> ids = Sets.newHashSet(id);
        List<Place> places = list(ids);

        return places.isEmpty() ? Place.NOT_EXIST_PLACE : places.get(0);
    }

    @Override
    public List<Place> list(Collection<Integer> ids) {
        if (ids.isEmpty()) return new ArrayList<Place>();

        String sql = "SELECT * FROM SG_Place WHERE Id IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        List<Place> places = queryList(sql, Place.class);

        if (!places.isEmpty()) {
            Map<Integer, List<PlaceImage>> placeImgsMap = queryImgs(ids);
            for (Place place : places) {
                List<PlaceImage> imgs = placeImgsMap.get(place.getId());
                place.setImgs(imgs == null ? new ArrayList<PlaceImage>() : imgs);
            }
        }

        return places;
    }

    private Map<Integer, List<PlaceImage>> queryImgs(Collection<Integer> ids) {
        String sql = "SELECT * FROM SG_PlaceImg WHERE PlaceId IN (" + StringUtils.join(ids, ",") + ") AND Status=1 ORDER BY AddTime DESC";
        List<PlaceImage> imgs = queryList(sql, PlaceImage.class);

        Map<Integer, List<PlaceImage>> imgsMap = new HashMap<Integer, List<PlaceImage>>();
        for (int id : ids) {
            imgsMap.put(id, new ArrayList<PlaceImage>());
        }
        for (PlaceImage img : imgs) {
            imgsMap.get(img.getPlaceId()).add(img);
        }

        return imgsMap;
    }
}
