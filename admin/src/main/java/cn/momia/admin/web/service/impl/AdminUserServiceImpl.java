package cn.momia.admin.web.service.impl;

import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.entity.AdminUser;
import cn.momia.admin.web.service.AdminUserService;
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
public class AdminUserServiceImpl implements AdminUserService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AdminUser get(int id) {
        String sql = "select id,username,password,status,addTime from t_admin where id = ? and status > ? ";
        final Object [] params = new Object[]{id,FinalUtil.DEL_STATUS};
        final AdminUser entity = new AdminUser();
        jdbcTemplate.query(sql,params, new RowCallbackHandler(){
            public void processRow(ResultSet rs) throws SQLException {
                entity.setId(rs.getInt("id"));
                entity.setUsername(rs.getString("username"));
                entity.setPassword(rs.getString("password"));
                entity.setStatus(rs.getInt("status"));
                entity.setAddTime(rs.getString("addTime"));
            }
        });

        return entity;
    }

    @Override
    public List<AdminUser> getEntitys() {
        List<AdminUser> reData = new ArrayList<AdminUser>();
        String sql = "select id,username,password,status,addTime from t_admin where status > ? order by id desc";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                AdminUser entity = new AdminUser();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setUsername(list.get(i).get("username").toString());
                entity.setPassword(list.get(i).get("password").toString());
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public int insert(AdminUser entity) {
        AdminUser user = isVerify(entity.getUsername(),entity.getPassword());
        int reData = 0;
        if (user.getId() > 0){
            reData = 0;
        }else {
            String sql = "insert into t_admin(username, password, status, addTime) value(?, ?, ?, NOW()) ";
            Object [] params = new Object[]{entity.getUsername(), entity.getPassword(), FinalUtil.ADD_STATUS};
            reData = jdbcTemplate.update(sql,params);
        }
        return reData;
    }

    @Override
    public int update(AdminUser entity) {
        String sql = "update t_admin set username = ? , password = ? where id = ? ";
        Object [] params = new Object[]{entity.getUsername(), entity.getPassword(), entity.getId()};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int delete(int id) {
        String sql = "update t_admin set status = ? where id = ? ";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS,id};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public AdminUser isVerify(String username, String password) {

        String sql = "select id,username,password,status,addTime from t_admin where username = ? and password = ? and status > ? ";
        final Object [] params = new Object[]{username,password,FinalUtil.DEL_STATUS};
        final AdminUser entity = new AdminUser();
        jdbcTemplate.query(sql,params, new RowCallbackHandler(){
            public void processRow(ResultSet rs) throws SQLException {
                entity.setId(rs.getInt("id"));
                entity.setUsername(rs.getString("username"));
                entity.setPassword(rs.getString("password"));
                entity.setStatus(rs.getInt("status"));
                entity.setAddTime(rs.getString("addTime"));
            }
        });

        return entity;
    }

    @Override
    public AdminUser formEntity(HttpServletRequest req,int id) {
        AdminUser entity = new AdminUser();
        entity.setId(id);
        entity.setUsername(req.getParameter("username"));
        entity.setPassword(req.getParameter("password"));
        return entity;
    }

    @Override
    public List<AdminUser> getQueryPages(int start_row,int end_row) {
        List<AdminUser> reData = new ArrayList<AdminUser>();
        String sql = "select id,username,password,status,addTime from t_admin where status > ? order by id desc limit "+start_row+","+end_row;
        Object [] params = new Object[]{FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                AdminUser entity = new AdminUser();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setUsername(list.get(i).get("username").toString());
                entity.setPassword(list.get(i).get("password").toString());
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }
}
