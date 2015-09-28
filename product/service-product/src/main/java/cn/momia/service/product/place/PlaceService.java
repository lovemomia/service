package cn.momia.service.product.place;

import java.util.Collection;
import java.util.List;

public interface PlaceService {
    Place get(int id);
    List<Place> list(Collection<Integer> ids);
}
