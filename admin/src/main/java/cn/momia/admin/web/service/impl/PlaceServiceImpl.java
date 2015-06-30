package cn.momia.admin.web.service.impl;

import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.entity.Place;
import cn.momia.admin.web.service.PlaceService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hoze on 15/6/15.
 */
@Service
public class PlaceServiceImpl implements PlaceService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Place get(int id) {

        String sql = "select id,name,address,`desc`,lng,lat,status,addTime from t_place where id = ? and status > ? ";
        final Object [] params = new Object[]{id, FinalUtil.DEL_STATUS};
        final Place entity = new Place();
        jdbcTemplate.query(sql,params, new RowCallbackHandler(){
            public void processRow(ResultSet rs) throws SQLException {
                entity.setId(rs.getInt("id"));
                entity.setName(rs.getString("name"));
                entity.setAddress(rs.getString("address"));
                entity.setDesc(rs.getString("desc"));
                entity.setLng(rs.getFloat("lng"));
                entity.setLat(rs.getFloat("lat"));
                entity.setStatus(rs.getInt("status"));
                entity.setAddTime(rs.getString("addTime"));
            }
        });

        return entity;
    }

    @Override
    public List<Place> getEntitys() {
        List<Place> reData = new ArrayList<Place>();
        String sql = "select id,name,address,`desc`,lng,lat,status,addTime from t_place where status > ? order by id desc";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                Place entity = new Place();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setName(list.get(i).get("name").toString());
                entity.setAddress(list.get(i).get("address").toString());
                entity.setDesc(list.get(i).get("desc").toString());
                entity.setLng(Float.parseFloat(list.get(i).get("lng").toString()));
                entity.setLat(Float.parseFloat(list.get(i).get("lat").toString()));
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public int insert(Place entity) {
        String sql = "insert into t_place(name, address, `desc`, lng, lat, status,addTime) value(?, ?, ?, ?, ?, ?,NOW()) ";
        Object [] params = new Object[]{entity.getName(), entity.getAddress(), entity.getDesc(), entity.getLng(), entity.getLat(), FinalUtil.ADD_STATUS};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int insertKey(Place entity) {
        final Place place = entity;
        final String sql = "insert into t_place (name, address, `desc`, lng, lat, status) values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int reData = jdbcTemplate.update( new PreparedStatementCreator(){
            public java.sql.PreparedStatement createPreparedStatement(Connection conn) throws SQLException{

                int i = 0;
                java.sql.PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(++i, place.getName());
                ps.setString(++i, place.getAddress());
                ps.setString(++i, place.getDesc());
                ps.setFloat(++i, place.getLng());
                ps.setFloat(++i, place.getLat());
                ps.setInt(++i, FinalUtil.ADD_STATUS);
                return ps;
            }
        },keyHolder);
        if (reData > 0) {
            reData = keyHolder.getKey().intValue();
        }
        return reData;

    }

    @Override
    public int update(Place entity) {
        String sql = "update t_place set name = ? , address = ?, `desc` = ?, lng = ?, lat = ? where id = ? ";
        Object [] params = new Object[]{entity.getName(), entity.getAddress(), entity.getDesc(), entity.getLng(), entity.getLat(), entity.getId()};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int delete(int id) {
        String sql = "update t_place set status = ? where id = ? ";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS,id};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public Place formEntity(HttpServletRequest request, int id) {
        Place entity = new Place();
        entity.setId(id);
        entity.setName(request.getParameter("name"));
        entity.setAddress(request.getParameter("address"));
        entity.setDesc(request.getParameter("desc"));
        entity.setLng(Float.parseFloat(request.getParameter("lng")));
        entity.setLat(Float.parseFloat(request.getParameter("lat")));
        return entity;
    }

    @Override
    public List<Place> getQueryPages(int start_row,int end_row) {
        List<Place> reData = new ArrayList<Place>();
        String sql = "select id,name,address,`desc`,lng,lat,status,addTime from t_place where status > ? order by id desc limit "+start_row+","+end_row;
        Object [] params = new Object[]{FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                Place entity = new Place();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setName(list.get(i).get("name").toString());
                entity.setAddress(list.get(i).get("address").toString());
                entity.setDesc(list.get(i).get("desc").toString());
                entity.setLng(Float.parseFloat(list.get(i).get("lng").toString()));
                entity.setLat(Float.parseFloat(list.get(i).get("lat").toString()));
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }
}
