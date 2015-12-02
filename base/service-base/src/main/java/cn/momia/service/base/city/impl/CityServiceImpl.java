package cn.momia.service.base.city.impl;

import cn.momia.api.base.dto.City;
import cn.momia.common.service.AbstractService;
import cn.momia.service.base.city.CityService;

import java.util.ArrayList;
import java.util.List;

public class CityServiceImpl extends AbstractService implements CityService {
    private List<City> citiesCache = new ArrayList<City>();

    @Override
    protected void doReload() {
        String sql = "SELECT Id, Name FROM SG_City WHERE Status<>0";
        citiesCache = queryObjectList(sql, City.class);
    }

    @Override
    public List<City> listAll() {
        if (isOutOfDate()) reload();
        return citiesCache;
    }
}
