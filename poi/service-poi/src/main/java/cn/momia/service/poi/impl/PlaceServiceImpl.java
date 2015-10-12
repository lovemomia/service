package cn.momia.service.poi.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.poi.Place;
import cn.momia.service.poi.PlaceImage;
import cn.momia.service.poi.PlaceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceServiceImpl extends DbAccessService implements PlaceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceServiceImpl.class);

    private static final String[] PLACE_FIELDS = { "Id", "CityId", "RegionId", "Name", "Address", "`Desc`", "Cover", "Lng", "Lat" };

    @Override
    public Place get(int id, int type) {
        String sql = "SELECT " + joinFields() + " FROM SG_Place WHERE Id=? AND Status=1";
        Place place = jdbcTemplate.query(sql, new Object[]{ id }, new ResultSetExtractor<Place>() {
            @Override
            public Place extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? buildPlace(rs) : Place.NOT_EXIST_PLACE;
            }
        });

        if (place.exists() && type == Place.Type.FULL) place.setImgs(getImgs(id));

        return place;
    }

    private String joinFields() {
        return StringUtils.join(PLACE_FIELDS, ",");
    }

    private Place buildPlace(ResultSet rs) throws SQLException {
        try {
            Place place = new Place();
            place.setId(rs.getInt("Id"));
            place.setCityId(rs.getInt("CityId"));
            place.setRegionId(rs.getInt("RegionId"));
            place.setName(rs.getString("Name"));
            place.setAddress(rs.getString("Address"));
            place.setDesc(rs.getString("Desc"));
            place.setCover(rs.getString("Cover"));
            place.setLng(rs.getDouble("Lng"));
            place.setLat(rs.getDouble("Lat"));

            return place;
        }
        catch (Exception e) {
            LOGGER.error("fail to build place: {}", rs.getLong("id"), e);
            return Place.NOT_EXIST_PLACE;
        }
    }

    private List<PlaceImage> getImgs(int id) {
        final List<PlaceImage> imgs = new ArrayList<PlaceImage>();
        String sql = "SELECT Url, Width, Height FROM SG_PlaceImg WHERE PlaceId=? AND Status=1 ORDER BY AddTime DESC";
        jdbcTemplate.query(sql, new Object[] { id }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                imgs.add(buildPlaceImage(rs));
            }
        });

        return imgs;
    }

    private PlaceImage buildPlaceImage(ResultSet rs) throws SQLException {
        PlaceImage img = new PlaceImage();
        img.setUrl(rs.getString("Url"));
        img.setWidth(rs.getInt("Width"));
        img.setHeight(rs.getInt("Height"));

        return img;
    }

    @Override
    public List<Place> list(Collection<Integer> ids, int type) {
        if (ids.isEmpty()) return new ArrayList<Place>();

        final List<Place> places = new ArrayList<Place>();
        String sql = "SELECT " + joinFields() + " FROM SG_Place WHERE Id IN (" + StringUtils.join(ids, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Place place = buildPlace(rs);
                if (place.exists()) places.add(place);
            }
        });

        if (!places.isEmpty() && type == Place.Type.FULL) {
            Map<Integer, List<PlaceImage>> placeImgsMap = getImgs(ids);
            for (Place place : places) {
                List<PlaceImage> imgs = placeImgsMap.get(place.getId());
                place.setImgs(imgs == null ? new ArrayList<PlaceImage>() : imgs);
            }
        }

        return places;
    }

    private Map<Integer, List<PlaceImage>> getImgs(Collection<Integer> ids) {
        final Map<Integer, List<PlaceImage>> placeImgsMap = new HashMap<Integer, List<PlaceImage>>();
        String sql = "SELECT PlaceId, Url, Width, Height FROM SG_PlaceImg WHERE PlaceId IN (" + StringUtils.join(ids, ",") + ") AND Status=1 ORDER BY AddTime DESC";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int placeId = rs.getInt("PlaceId");
                PlaceImage img = buildPlaceImage(rs);
                List<PlaceImage> imgs = placeImgsMap.get(placeId);
                if (imgs == null) {
                    imgs = new ArrayList<PlaceImage>();
                    placeImgsMap.put(placeId, imgs);
                }
                imgs.add(img);
            }
        });

        return placeImgsMap;
    }
}
