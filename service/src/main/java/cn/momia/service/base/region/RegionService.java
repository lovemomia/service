package cn.momia.service.base.region;

import java.util.List;

public interface RegionService {
    Region get(int id);
    List<Region> getAll();
}
