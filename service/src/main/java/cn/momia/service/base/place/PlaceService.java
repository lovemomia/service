package cn.momia.service.base.place;

import java.util.List;

public interface PlaceService {
    long add(long userId, Place place);
    List<Place> getPlaces(long userId, int start, int count);
    Place get(long id);
    boolean delete(long id);
    boolean updateName(long placeId, String name);
    boolean updateAddress(long placeId, String address);
    boolean updateDesc(long placeId, String desc);
    boolean updatePoi(long placeId, float lng, float lat);
    boolean addImage(long placeId, String url);
    boolean deleteImage(long placeId, long imageId);
}
