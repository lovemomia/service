package cn.momia.admin.web.service;

import cn.momia.admin.web.entity.AdminUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by hoze on 15/6/15.
 */
public interface AdminUserService {
    public AdminUser get(int id);
    public List<AdminUser> getEntitys();
    public List<AdminUser> getQueryPages(int start_row,int end_row);
    public int insert(AdminUser entity);
    public int update(AdminUser entity);
    public int delete(int id);
    public AdminUser isVerify(String username, String password);
    public AdminUser formEntity(HttpServletRequest req,int id);
}
