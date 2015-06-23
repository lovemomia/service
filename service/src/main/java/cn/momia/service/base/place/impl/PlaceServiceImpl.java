package cn.momia.service.base.place.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.place.Place;
import cn.momia.service.base.place.PlaceImage;
import cn.momia.service.base.place.PlaceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceServiceImpl extends DbAccessService implements PlaceService {
    @Override
    public Place get(long id) {
        String sql = "SELECT id, name, address, `desc`, lng, lat FROM t_place WHERE id=? AND status=1";

        Place place = jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Place>() {
            @Override
            public Place extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildPlace(rs);
                return Place.NOT_EXIST_PLACE;
            }
        });
        place.setImgs(getImgUrls(place.getId()));

        return place;
    }

    private Place buildPlace(ResultSet rs) throws SQLException {
        Place place = new Place();

        place.setId(rs.getLong("id"));
        place.setName(rs.getString("name"));
        place.setAddress(rs.getString("address"));
        place.setDesc(rs.getString("desc"));
        place.setLng(rs.getFloat("lng"));
        place.setLat(rs.getFloat("lat"));

        return place;
    }

    private List<PlaceImage> getImgUrls(long placeId) {
        final List<PlaceImage> imgs = new ArrayList<PlaceImage>();

        String sql = "SELECT url, width, height FROM t_place_img WHERE placeId=? AND status=1";
        jdbcTemplate.query(sql, new Object[] { placeId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                imgs.add(buildPlaceImage(rs));
            }
        });

        return imgs;
    }

    private PlaceImage buildPlaceImage(ResultSet rs) throws SQLException {
        PlaceImage img = new PlaceImage();
        img.setUrl(rs.getString("url"));
        img.setWidth(rs.getInt("width"));
        img.setHeight(rs.getInt("height"));

        return img;
    }

    @Override
    public Place queryByProduct(long productId) {
        String sql = "SELECT placeId FROM t_product WHERE productId=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { productId }, new ResultSetExtractor<Place>() {
            @Override
            public Place extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return get(rs.getLong("placeId"));
                return Place.NOT_EXIST_PLACE;
            }
        });
    }

    @Override
    public Map<Long, Place> queryByProducts(List<Long> productIds) {
        final Map<Long, Place> places = new HashMap<Long, Place>();
        if (productIds.isEmpty()) return places;

        String sql = "SELECT A.id, A.name, A.address, A.`desc`, A.lng, A.lat, B.productId FROM t_place A INNER JOIN t_product B WHERE B.productId IN (" + StringUtils.join(productIds, ",") + ") AND A.status=1 AND B.status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long productId = rs.getLong("productId");
                Place place = buildPlace(rs);
                places.put(productId, place);
            }
        });

        return places;
    }
}
