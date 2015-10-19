package cn.momia.service.base.city.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.base.city.City;
import cn.momia.service.base.city.CityService;

import java.util.ArrayList;
import java.util.List;

public class CityServiceImpl extends DbAccessService implements CityService {
    private List<City> citiesCache = new ArrayList<City>();

    @Override
    protected void doReload() {
        String sql = "SELECT Id, Name FROM SG_City WHERE Status=1";
        citiesCache = queryList(sql, City.class);
    }

    @Override
    public List<City> listAll() {
        if (isOutOfDate()) reload();
        return citiesCache;
    }
}
