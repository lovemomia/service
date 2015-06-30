package cn.momia.admin.web.service.impl;

import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.entity.SkuProperty;
import cn.momia.admin.web.service.SkuPropertyService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hoze on 15/6/15.
 */
@Service
public class SkuPropertyServiceImpl implements SkuPropertyService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SkuProperty get(int id) {
        String sql = "select id,categoryId,name,status,addTime from t_sku_property_name where id = ? and status > ? ";
        final Object [] params = new Object[]{id, FinalUtil.DEL_STATUS};
        final SkuProperty entity = new SkuProperty();
        jdbcTemplate.query(sql,params, new RowCallbackHandler(){
            public void processRow(ResultSet rs) throws SQLException {
                entity.setId(rs.getInt("id"));
                entity.setCategoryId(rs.getInt("categoryId"));
                entity.setName(rs.getString("name"));
                entity.setStatus(rs.getInt("status"));
                entity.setAddTime(rs.getString("addTime"));
            }
        });

        return entity;
    }

    @Override
    public List<SkuProperty> getEntitys() {
        List<SkuProperty> reData = new ArrayList<SkuProperty>();
        String sql = "select id,categoryId,name,status,addTime from t_sku_property_name where status > ? order by id desc";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                SkuProperty entity = new SkuProperty();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setCategoryId(Integer.parseInt(list.get(i).get("categoryId").toString()));
                entity.setName(list.get(i).get("name").toString());
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public List<SkuProperty> getEntitysByKey(int categoryId) {
        List<SkuProperty> reData = new ArrayList<SkuProperty>();
        String sql = "select id,categoryId,name,status,addTime from t_sku_property_name where categoryId = ? and status > ? ";
        Object [] params = new Object[]{categoryId, FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                SkuProperty entity = new SkuProperty();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setCategoryId(Integer.parseInt(list.get(i).get("categoryId").toString()));
                entity.setName(list.get(i).get("name").toString());
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public int insert(SkuProperty entity) {
        String sql = "insert into t_sku_property_name(categoryId,name,status,addTime) value(?, ?, ?, NOW())";
        Object [] params = new Object[]{entity.getCategoryId(), entity.getName(), FinalUtil.ADD_STATUS};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int update(SkuProperty entity) {
        String sql = "update t_sku_property_name set categoryId = ? , name = ? where id = ? ";
        Object [] params = new Object[]{entity.getCategoryId(), entity.getName(), entity.getId()};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int delete(int id) {
        String sql = "update t_sku_property_name set status = ? where id = ? ";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS,id};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public SkuProperty formEntity(HttpServletRequest req,int id) {
        SkuProperty entity = new SkuProperty();
        entity.setId(id);
        entity.setCategoryId(Integer.parseInt(req.getParameter("categoryId")));
        entity.setName(req.getParameter("name"));
        return entity;
    }
}
