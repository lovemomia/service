package cn.momia.service.base.sort.impl;

import cn.momia.api.base.dto.SortType;
import cn.momia.common.service.AbstractService;
import cn.momia.service.base.sort.SortTypeService;

import java.util.ArrayList;
import java.util.List;

public class SortTypeServiceImpl extends AbstractService implements SortTypeService {
    private List<SortType> sortTypesCache = new ArrayList<SortType>();

    @Override
    protected void doReload() {
        String sql = "SELECT Id, Text FROM SG_SortType WHERE Status<>0";
        sortTypesCache = queryObjectList(sql, SortType.class);
    }

    @Override
    public List<SortType> listAll() {
        if (isOutOfDate()) reload();
        return sortTypesCache;
    }
}
