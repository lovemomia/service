package cn.momia.admin.web.service.impl;

import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.entity.Category;
import cn.momia.admin.web.service.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Category get(int id) {
        String sql = "select id,name,parentId,status,addTime from t_category where id = ? and status > ? ";
        final Object [] params = new Object[]{id, FinalUtil.DEL_STATUS};
        final Category entity = new Category();
        jdbcTemplate.query(sql,params, new RowCallbackHandler(){
            public void processRow(ResultSet rs) throws SQLException {
                entity.setId(rs.getInt("id"));
                entity.setParentId(rs.getInt("parentId"));
                entity.setName(rs.getString("name"));
                entity.setStatus(rs.getInt("status"));
                entity.setAddTime(rs.getString("addTime"));
            }
        });

        return entity;
    }

    @Override
    public List<Category> getModels(int parentId) {
        List<Category> reData = new ArrayList<Category>();
        String sql = "select id,name,parentId,status,addTime from t_category where parentId = ? and status > ? ";
        Object [] params = new Object[]{parentId, FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                Category entity = new Category();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setParentId(Integer.parseInt(list.get(i).get("parentId").toString()));
                entity.setName(list.get(i).get("name").toString());
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public List<Category> getEntitys() {
        List<Category> reData = new ArrayList<Category>();
        String sql = "select id,name,parentId,status,addTime from t_category where status > ? order by id desc";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                Category entity = new Category();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setParentId(Integer.parseInt(list.get(i).get("parentId").toString()));
                entity.setName(list.get(i).get("name").toString());
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public int insert(Category entity) {
        String sql = "insert into t_category(name, parentId, status, addTime) value(?, ?, ?, NOW()) ";
        Object [] params = new Object[]{entity.getName(), entity.getParentId(), FinalUtil.ADD_STATUS};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int update(Category entity) {
        String sql = "update t_category set name = ? , parentId = ? where id = ? ";
        Object [] params = new Object[]{entity.getName(), entity.getParentId(), entity.getId()};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int delete(int id) {
        String sql = "update t_category set status = ? where id = ? ";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS,id};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public Category formEntity(HttpServletRequest req,int id) {
        Category entity = new Category();
        entity.setId(id);
        entity.setParentId(Integer.parseInt(req.getParameter("parentId")));
        entity.setName(req.getParameter("name"));
        return entity;
    }

    @Override
    public List<Category> getEntities(List<Category> categories){
        if(categories.size() > 0){
            for (int i = 0; i <categories.size() ; i++) {
                Category entity = categories.get(i);
                int parentId = entity.getParentId();
                if (parentId == 0) {
                    entity.setParentname("顶级分类");
                }else{
                    entity.setParentname(this.get(parentId).getParentname());
                }
            }
        }

        return categories;
    }


    @Override
    public List<Category> getQueryPages(int start_row,int end_row) {
        List<Category> reData = new ArrayList<Category>();
        String sql = "select id,name,parentId,status,addTime from t_category where status > ? order by id desc limit "+start_row+","+end_row;
        Object [] params = new Object[]{FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                Category entity = new Category();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setParentId(Integer.parseInt(list.get(i).get("parentId").toString()));
                entity.setName(list.get(i).get("name").toString());
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }
}
