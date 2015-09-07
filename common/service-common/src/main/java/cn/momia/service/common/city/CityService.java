package cn.momia.service.common.city;

import java.util.List;

public interface CityService {
    City get(int id);
    List<City> getAll();
}
