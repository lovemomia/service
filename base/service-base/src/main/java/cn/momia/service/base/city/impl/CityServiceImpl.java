package cn.momia.service.base.city.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.base.city.City;
import cn.momia.service.base.city.CityService;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityServiceImpl extends DbAccessService implements CityService {
    private List<City> citiesCache = new ArrayList<City>();
    private Map<Integer, Integer> citysMap = new HashMap<Integer, Integer>();

    @Override
    protected void doReload() {
        final List<City> newCitiesCache = new ArrayList<City>();
        final Map<Integer, Integer> newCitysMap = new HashMap<Integer, Integer>();

        String sql = "SELECT id, name FROM t_city WHERE status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                City city = new City();
                city.setId(rs.getInt("id"));
                city.setName(rs.getString("name"));
                newCitiesCache.add(city);
                newCitysMap.put(city.getId(), newCitiesCache.size() - 1);
            }
        });

        citiesCache = newCitiesCache;
        citysMap = newCitysMap;
    }

    @Override
    public City get(int id) {
        if (isOutOfDate()) reload();

        Integer index = citysMap.get(id);
        if (index == null) return City.NOT_EXIST_CITY;

        return citiesCache.get(index);
    }

    @Override
    public List<City> listAll() {
        if (isOutOfDate()) reload();
        return citiesCache;
    }
}
