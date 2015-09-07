package cn.momia.service.common.region;

import java.util.List;

public interface RegionService {
    Region get(int id);
    List<Region> getAll();
}
