package cn.momia.service.base.product.place.impl;

import cn.momia.service.common.DbAccessService;
import cn.momia.service.base.product.place.Place;
import cn.momia.service.base.product.place.PlaceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlaceServiceImpl extends DbAccessService implements PlaceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceServiceImpl.class);
    private static final String[] PLACE_FIELDS = { "id", "cityId", "regionId", "name", "address", "`desc`", "lng", "lat" };

    @Override
    public Place get(long id) {
        String sql = "SELECT " + joinFields() + " FROM t_place WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[]{id}, new ResultSetExtractor<Place>() {
            @Override
            public Place extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildPlace(rs);
                return Place.NOT_EXIST_PLACE;
            }
        });
    }

    private String joinFields() {
        return StringUtils.join(PLACE_FIELDS, ",");
    }

    private Place buildPlace(ResultSet rs) throws SQLException {
        try {
            Place place = new Place();
            place.setId(rs.getLong("id"));
            place.setCityId(rs.getInt("cityId"));
            place.setRegionId(rs.getInt("regionId"));
            place.setName(rs.getString("name"));
            place.setAddress(rs.getString("address"));
            place.setDesc(rs.getString("desc"));
            place.setLng(rs.getDouble("lng"));
            place.setLat(rs.getDouble("lat"));

            return place;
        }
        catch (Exception e) {
            LOGGER.error("fail to build place: {}", rs.getLong("id"), e);
            return Place.INVALID_PLACE;
        }
    }

    @Override
    public Map<Long, Place> get(Collection<Long> ids) {
        final Map<Long, Place> places = new HashMap<Long, Place>();
        if (ids == null || ids.isEmpty()) return places;

        String sql = "SELECT " + joinFields() + " FROM t_place WHERE id IN (" + StringUtils.join(ids, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Place place = buildPlace(rs);
                if (place.exists()) places.put(place.getId(), place);
            }
        });

        return places;
    }
}
