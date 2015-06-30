package cn.momia.admin.web.service.impl;

import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.entity.Images;
import cn.momia.admin.web.entity.ProductImg;
import cn.momia.admin.web.service.ProductImgService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hoze on 15/6/15.
 */
@Service
public class ProductImgServiceImpl implements ProductImgService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ProductImg get(int id) {
        String sql = "select id,productId,url,width,height,status,addTime from t_product_img where id = ? and status > ? ";
        final Object [] params = new Object[]{id, FinalUtil.DEL_STATUS};
        final ProductImg entity = new ProductImg();
        jdbcTemplate.query(sql,params, new RowCallbackHandler(){
            public void processRow(ResultSet rs) throws SQLException {
                entity.setId(rs.getInt("id"));
                entity.setProductId(rs.getInt("productId"));
                entity.setUrl(rs.getString("url"));
                entity.setWidth(rs.getInt("width"));
                entity.setHeight(rs.getInt("height"));
                entity.setStatus(rs.getInt("status"));
                entity.setAddTime(rs.getString("addTime"));
            }
        });

        return entity;
    }

    @Override
    public List<ProductImg> getEntitys() {
        List<ProductImg> reData = new ArrayList<ProductImg>();
        String sql = "select id,productId,url,width,height,status,addTime from t_product_img where status > ? ";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                ProductImg entity = new ProductImg();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setProductId(Integer.parseInt(list.get(i).get("productId").toString()));
                entity.setUrl(list.get(i).get("url").toString());
                entity.setWidth(Integer.parseInt(list.get(i).get("width").toString()));
                entity.setHeight(Integer.parseInt(list.get(i).get("height").toString()));
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public List<ProductImg> getEntitysByKey(int productId) {
        List<ProductImg> reData = new ArrayList<ProductImg>();
        String sql = "select id,productId,url,width,height,status,addTime from t_product_img where productId = ? and status > ? ";
        Object [] params = new Object[]{productId, FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                ProductImg entity = new ProductImg();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setProductId(Integer.parseInt(list.get(i).get("productId").toString()));
                entity.setUrl(list.get(i).get("url").toString());
                entity.setWidth(Integer.parseInt(list.get(i).get("width").toString()));
                entity.setHeight(Integer.parseInt(list.get(i).get("height").toString()));
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public int insert(ProductImg entity) {
        String sql = "insert into t_product_img(productId, url, width, height, status, addTime) value(?, ?, ?, ?, ?, NOW()) ";
        Object [] params = new Object[]{entity.getProductId(), entity.getUrl(), entity.getWidth(), entity.getHeight(), FinalUtil.ADD_STATUS};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int update(ProductImg entity) {
        String sql = "update t_product_img set url = ? , productId = ?, width = ?, height = ? where id = ? ";
        Object [] params = new Object[]{entity.getUrl(), entity.getProductId(), entity.getWidth(), entity.getHeight(), entity.getId()};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int delete(int id) {
        String sql = "update t_product_img set status = ? where id = ? ";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS,id};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public ProductImg formEntity(Images img, int id) {
        ProductImg entity = new ProductImg();
        entity.setProductId(id);
        entity.setUrl(img.getUrl());
        entity.setWidth(img.getWidth());
        entity.setHeight(img.getHeigth());
        return entity;
    }
}
