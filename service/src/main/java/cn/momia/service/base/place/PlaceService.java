package cn.momia.service.base.place;

import java.util.List;
import java.util.Map;

public interface PlaceService {
    Place get(long id);
    Map<Long, Place> getByProduct(List<Long> productIds);
}
