package cn.momia.admin.web.service;

import cn.momia.admin.web.entity.Category;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by hoze on 15/6/15.
 */
public interface CategoryService {
    public Category get(int id);
    public List<Category> getModels(int parentId);
    public List<Category> getEntitys();
    public List<Category> getEntities(List<Category> categories);
    public List<Category> getQueryPages(int start_row,int end_row);
    public int insert(Category entity);
    public int update(Category entity);
    public int delete(int id);
    public Category formEntity(HttpServletRequest request,int id);
}
