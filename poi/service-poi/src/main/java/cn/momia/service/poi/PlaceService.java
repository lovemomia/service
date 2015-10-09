package cn.momia.service.poi;

import java.util.Collection;
import java.util.List;

public interface PlaceService {
    Place get(int id, int type);
    List<Place> list(Collection<Integer> ids, int type);
}
