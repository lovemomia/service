package cn.momia.admin.web.service;

import cn.momia.admin.web.entity.City;

import java.util.List;

/**
 * Created by hoze on 15/6/25.
 */
public interface CityService {

    public List<City> getEntitys();
    public City get(int id);
}
