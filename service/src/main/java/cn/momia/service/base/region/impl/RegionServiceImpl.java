package cn.momia.service.base.region.impl;

import cn.momia.service.base.region.Region;
import cn.momia.service.base.region.RegionService;
import cn.momia.service.common.DbAccessService;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionServiceImpl extends DbAccessService implements RegionService {
    private List<Region> regionsCache;
    private Map<Integer, Integer> regionsMap;

    public void init() {
        regionsCache = new ArrayList<Region>();
        regionsMap = new HashMap<Integer, Integer>();

        String sql = "SELECT id, cityId, name, parentId FROM t_region WHERE status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Region region = new Region();
                region.setId(rs.getInt("id"));
                region.setCityId(rs.getInt("cityId"));
                region.setName(rs.getString("name"));
                region.setParentId(rs.getInt("parentId"));
                regionsCache.add(region);
                regionsMap.put(region.getId(), regionsCache.size() - 1);
            }
        });
    }
    @Override
    public Region get(int id) {
        Integer index = regionsMap.get(id);
        if (index == null) return Region.NOT_EXIST_REGION;

        return regionsCache.get(index);
    }

    @Override
    public List<Region> getAll() {
        return regionsCache;
    }
}
