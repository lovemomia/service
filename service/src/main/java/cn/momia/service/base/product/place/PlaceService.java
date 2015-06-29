package cn.momia.service.base.product.place;

import java.util.List;
import java.util.Map;

public interface PlaceService {
    Place get(long id);
    Map<Long, Place> get(List<Long> ids);
}
