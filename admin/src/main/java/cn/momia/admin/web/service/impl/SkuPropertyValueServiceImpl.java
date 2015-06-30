package cn.momia.admin.web.service.impl;

import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.entity.SkuPropertyValue;
import cn.momia.admin.web.service.SkuPropertyValueService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hoze on 15/6/15.
 */
@Service
public class SkuPropertyValueServiceImpl implements SkuPropertyValueService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SkuPropertyValue get(int id) {
        String sql = "select id,nameId,value,status,addTime from t_sku_property_value where id = ? and status > ? ";
        final Object [] params = new Object[]{id, FinalUtil.DEL_STATUS};
        final SkuPropertyValue entity = new SkuPropertyValue();
        jdbcTemplate.query(sql,params, new RowCallbackHandler(){
            public void processRow(ResultSet rs) throws SQLException {
                entity.setId(rs.getInt("id"));
                entity.setNameId(rs.getInt("nameId"));
                entity.setValue(rs.getString("value"));
                entity.setStatus(rs.getInt("status"));
                entity.setAddTime(rs.getString("addTime"));
            }
        });

        return entity;
    }

    @Override
    public List<SkuPropertyValue> getEntitys() {
        List<SkuPropertyValue> reData = new ArrayList<SkuPropertyValue>();
        String sql = "select id,nameId,value,status,addTime from t_sku_property_value where status > ? ";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                SkuPropertyValue entity = new SkuPropertyValue();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setNameId(Integer.parseInt(list.get(i).get("nameId").toString()));
                entity.setValue(list.get(i).get("value").toString());
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public List<SkuPropertyValue> getEntitysByKey(int propertyId) {
        List<SkuPropertyValue> reData = new ArrayList<SkuPropertyValue>();
        String sql = "select id,nameId,value,status,addTime from t_sku_property_value where nameId = ? and status > ? ";
        Object [] params = new Object[]{propertyId, FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                SkuPropertyValue entity = new SkuPropertyValue();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setNameId(Integer.parseInt(list.get(i).get("nameId").toString()));
                entity.setValue(list.get(i).get("value").toString());
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public int insert(SkuPropertyValue entity) {
        String sql = "insert into t_sku_property_value(nameId,value,status,addTime) value(?, ?, ?, NOW()) ";
        Object [] params = new Object[]{entity.getNameId(), entity.getValue(), FinalUtil.ADD_STATUS};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int update(SkuPropertyValue entity) {
        String sql = "update t_sku_property_value set nameId = ? , value = ? where id = ? ";
        Object [] params = new Object[]{entity.getNameId(), entity.getValue(), entity.getId()};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int delete(int id) {
        String sql = "update t_sku_property_value set status = ? where id = ? ";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS,id};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public SkuPropertyValue formEntity(HttpServletRequest req, int id) {
        SkuPropertyValue entity = new SkuPropertyValue();
        entity.setNameId(id);
        entity.setValue(req.getParameter("value"));
        return entity;
    }
}
