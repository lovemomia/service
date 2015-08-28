package cn.momia.service.product.place;

import cn.momia.service.base.Service;

import java.util.Collection;
import java.util.List;

public interface PlaceService extends Service {
    Place get(int id);
    List<Place> get(Collection<Integer> ids);
}
