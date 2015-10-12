package cn.momia.service.base.sort.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.base.sort.SortType;
import cn.momia.service.base.sort.SortTypeService;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SortTypeServiceImpl extends DbAccessService implements SortTypeService {
    private List<SortType> sortTypesCache = new ArrayList<SortType>();

    @Override
    protected void doReload() {
        final List<SortType> newSortTypesCache = new ArrayList<SortType>();

        String sql = "SELECT Id, Text FROM SG_SortType WHERE Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                SortType sortType = new SortType();
                sortType.setId(rs.getInt("Id"));
                sortType.setText(rs.getString("Text"));
                newSortTypesCache.add(sortType);
            }
        });

        sortTypesCache = newSortTypesCache;
    }

    @Override
    public List<SortType> listAll() {
        if (isOutOfDate()) reload();
        return sortTypesCache;
    }
}
