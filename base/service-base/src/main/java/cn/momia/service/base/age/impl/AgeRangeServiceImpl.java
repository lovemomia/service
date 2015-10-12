package cn.momia.service.base.age.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.base.age.AgeRange;
import cn.momia.service.base.age.AgeRangeService;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AgeRangeServiceImpl extends DbAccessService implements AgeRangeService {
    private List<AgeRange> ageRangesCache = new ArrayList<AgeRange>();

    @Override
    protected void doReload() {
        final List<AgeRange> newAgeRangesCache = new ArrayList<AgeRange>();

        String sql = "SELECT Id, `Min`, `Max` FROM SG_AgeRange WHERE Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                AgeRange ageRange = new AgeRange();
                ageRange.setId(rs.getInt("Id"));
                ageRange.setMin(rs.getInt("Min"));
                ageRange.setMax(rs.getInt("Max"));
                newAgeRangesCache.add(ageRange);
            }
        });

        ageRangesCache = newAgeRangesCache;
    }

    @Override
    public List<AgeRange> listAll() {
        if (isOutOfDate()) reload();
        return ageRangesCache;
    }
}
