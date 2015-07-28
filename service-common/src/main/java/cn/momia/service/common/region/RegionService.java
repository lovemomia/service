package cn.momia.service.common.region;

import cn.momia.common.service.Service;

import java.util.List;

public interface RegionService extends Service {
    Region get(int id);
    List<Region> getAll();
}
