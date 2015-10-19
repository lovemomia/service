package cn.momia.service.base.region.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.base.region.Region;
import cn.momia.service.base.region.RegionService;

import java.util.ArrayList;
import java.util.List;

public class RegionServiceImpl extends DbAccessService implements RegionService {
    private List<Region> regionsCache = new ArrayList<Region>();

    @Override
    protected void doReload() {
        String sql = "SELECT Id, CityId, Name, ParentId FROM SG_Region WHERE Status=1";
        regionsCache = queryList(sql, Region.class);
    }

    @Override
    public List<Region> listAll() {
        if (isOutOfDate()) reload();
        return regionsCache;
    }
}
