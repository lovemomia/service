package cn.momia.service.product.place;

import cn.momia.service.base.Service;

import java.util.Collection;
import java.util.Map;

public interface PlaceService extends Service {
    Place get(int id);
    Map<Integer, Place> get(Collection<Integer> ids);
}
