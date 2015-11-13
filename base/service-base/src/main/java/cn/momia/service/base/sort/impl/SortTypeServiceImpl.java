package cn.momia.service.base.sort.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.base.sort.SortType;
import cn.momia.service.base.sort.SortTypeService;

import java.util.ArrayList;
import java.util.List;

public class SortTypeServiceImpl extends AbstractService implements SortTypeService {
    private List<SortType> sortTypesCache = new ArrayList<SortType>();

    @Override
    protected void doReload() {
        String sql = "SELECT Id, Text FROM SG_SortType WHERE Status=1";
        sortTypesCache = queryList(sql, SortType.class);
    }

    @Override
    public List<SortType> listAll() {
        if (isOutOfDate()) reload();
        return sortTypesCache;
    }
}
