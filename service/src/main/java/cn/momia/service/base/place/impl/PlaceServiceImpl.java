package cn.momia.service.base.place.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.place.Place;
import cn.momia.service.base.place.PlaceService;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 技师位置信息
 */
public class PlaceServiceImpl extends DbAccessService implements PlaceService {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public long add(final long userId, final Place place) {
        final String sql = "insert into t_place (name,address,lng,lat,addTime,userId,`desc`) values (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        long flag = jdbcTemplate.update(new PreparedStatementCreator() {
            public java.sql.PreparedStatement createPreparedStatement(Connection conn) throws SQLException {

                int i = 0;
                java.sql.PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(++i, place.getName());
                ps.setString(++i, place.getAddress());
                ps.setFloat(++i, place.getLng());
                ps.setFloat(++i, place.getLat());
                ps.setString(++i, sdf.format(place.getAddTime()));
                ps.setLong(++i, userId);
                ps.setString(++i, place.getDesc());

                return ps;
            }
        }, keyHolder);
        if (flag > 0) {
            flag = keyHolder.getKey().intValue();
            List<String> list = place.getImgs();
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    boolean bl = addImage(flag, list.get(i));
                    if (bl == false) {
                        break;
                    }
                }
            }

        }
        return flag;
    }

    @Override
    public List<Place> getPlaces(long userId, int start, int count) {

        String sql = "select id,name,address,lng,lat,status,addTime,updateTime,`desc` from t_place where 1=1 and userId = ? ";

        sql += " limit " + start + "," + count;

        List<Place> ls = new ArrayList<Place>();
        Object[] params = new Object[] { userId };
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        for (int i = 0; i < list.size(); i++) {
            Place entity = new Place();
            long id = Integer.parseInt(list.get(i).get("id").toString());
            entity.setId(id);
            entity.setName(list.get(i).get("name").toString());
            entity.setAddress(list.get(i).get("address").toString());
            entity.setLng(Float.parseFloat(list.get(i).get("lng").toString()));
            entity.setLat(Float.parseFloat(list.get(i).get("lat").toString()));
            entity.setImgs(getImgUrls(id));
            entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
            try {
                entity.setAddTime(sdf.parse(list.get(i).get("addTime").toString()));
                entity.setUpdateTime(sdf.parse(list.get(i).get("updateTime").toString()));
            } catch (ParseException _ex) {
                _ex.printStackTrace();
            }
            entity.setDesc(list.get(i).get("desc").toString());
            ls.add(entity);
        }
        return ls;
    }

    @Override
    public Place get(long id) {
        String sql = "select id,name,address,lng,lat,status,addTime,updateTime,`desc` from t_place where id = ?";

        final Place entity = new Place();
        final Object[] params = new Object[] { id };

        jdbcTemplate.query(sql, params, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                long id = rs.getInt("id");
                entity.setId(id);
                entity.setName(rs.getString("name"));
                entity.setAddress(rs.getString("address"));
                entity.setLng(rs.getFloat("lng"));
                entity.setLat(rs.getFloat("lat"));
                entity.setImgs(getImgUrls(id));
                entity.setStatus(rs.getInt("status"));
                entity.setAddTime(rs.getDate("addTime"));
                entity.setUpdateTime(rs.getDate("updateTime"));
                entity.setDesc(rs.getString("desc"));
            }
        });

        return entity;
    }

    @Override
    public boolean delete(long id) {

        String sql = "update t_place set status=0 where id = ? ";
        Object[] params = new Object[] { id };
        int flag = jdbcTemplate.update(sql, params);
        if (flag > 0)
            return delImage(id);
        else
            return false;
    }

    @Override
    public boolean updateName(long placeId, String name) {
        String sql = "update t_place set name = ? where id = ?";

        Object[] params = new Object[] { name, placeId };
        int flag = jdbcTemplate.update(sql, params);
        if (flag > 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean updateAddress(long placeId, String address) {
        String sql = "update t_place set address = ? where id = ?";

        Object[] params = new Object[] { address, placeId };
        int flag = jdbcTemplate.update(sql, params);
        if (flag > 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean updateDesc(long placeId, String desc) {

        String sql = "update t_place set `desc` = ? where id = ?";

        Object[] params = new Object[] { desc, placeId };
        int flag = jdbcTemplate.update(sql, params);
        if (flag > 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean updatePoi(long placeId, float lng, float lat) {

        String sql = "update t_place set lng = ?,lat = ?  where id = ?";

        Object[] params = new Object[] { lng, lat, placeId };
        int flag = jdbcTemplate.update(sql, params);
        if (flag > 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean addImage(long placeId, String url) {
        String sql = "insert into t_place_img (placeId,picUrl,addTime) values (?, ?, ?)";
        Object[] params = new Object[] { placeId, url, sdf.format(new Date()) };
        int flag = jdbcTemplate.update(sql, params);
        if (flag > 0)
            return true;
        else
            return false;
    }

    private boolean delImage(long placeId) {
        String sql = "update t_place_img set status=0 where placeId = ? ";
        Object[] params = new Object[] { placeId };
        int flag = jdbcTemplate.update(sql, params);
        if (flag > 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean deleteImage(long placeId, long imageId) {
        String sql = "update t_place_img set status=0 where placeId = ? and id = ? ";
        //String sql = "delete from t_place_img where placeId = ? and id = ? ";
        Object[] params = new Object[] { placeId, imageId };
        int flag = jdbcTemplate.update(sql, params);
        if (flag > 0)
            return true;
        else
            return false;
    }

    /**
     * 获取位置图片url列表
     *
     * @param placeId
     * @return
     */
    private List<String> getImgUrls(long placeId) {
        List<String> ls = new ArrayList<String>();
        String sql = "select picUrl from t_place_img where placeId = ?  ";
        Object[] params = new Object[] { placeId };
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        for (int i = 0; i < list.size(); i++) {
            ls.add(list.get(i).get("picUrl").toString());
        }
        return ls;
    }
}
