package cn.momia.service.base.product.place;

import java.util.Collection;
import java.util.Map;

public interface PlaceService {
    Place get(long id);
    Map<Long, Place> get(Collection<Long> ids);
}
