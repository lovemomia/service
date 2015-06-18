package cn.momia.service.base.place;

import java.util.List;
import java.util.Map;

public interface PlaceService {
    Place get(long id);
    Place getByProduct(long productId);
    Map<Long, Place> queryByProducts(List<Long> productIds);
}
