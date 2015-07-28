package cn.momia.service.common.city;

import cn.momia.common.service.Service;

import java.util.List;

public interface CityService extends Service {
    City get(int id);
    List<City> getAll();
}
